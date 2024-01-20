
import os
import uuid
from datetime import datetime, timedelta
import bcrypt
from flask import Flask, jsonify, request
from mysql.connector import pooling
import requests
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
import jwt
import py_eureka_client.eureka_client as eureka_client


app = Flask(__name__)



# db_config = {
#     "host": os.environ.get("DATABASE_HOST", "localhost"),
#     "port": os.environ.get("DATABASE_PORT", "3306"),
#     "database": os.environ.get("DATABASE_NAME", "fuel"),
#     "user": os.environ.get("DATABASE_USER", "root"),
#     "password": os.environ.get("DATABASE_PASS", ""),
# }

db_config = {
    "host": os.environ.get("DATABASE_HOST", "mysql-container1"),
    "port": os.environ.get("DATABASE_PORT", "3306"),
    "database": os.environ.get("DATABASE_NAME", "fuel"),
    "user": os.environ.get("DATABASE_USER", "root"),
    "password": os.environ.get("DATABASE_PASS", "root"),
}



db_pool = pooling.MySQLConnectionPool(pool_name="mypool", pool_size=5, **db_config)


private_key_path = os.path.join(os.path.dirname(__file__), 'private.pem')
private_key = serialization.load_pem_private_key(open(private_key_path, 'rb').read(), password=None)


eureka_server_url = "http://eureka:8010/eureka"
rest_port = 8070
eureka_client.init(
    eureka_server=eureka_server_url,
    app_name="fuel-flask",
    instance_host="localhost",
    instance_port=rest_port
)


login_query = "SELECT id, password FROM user WHERE email = %s"
signup_query = "INSERT INTO user (email, nom, password, prenom) VALUES (%s, %s, %s, %s)"
signup_id_query = "SELECT id FROM user WHERE email = %s"


def execute_query(query, params=None, fetchone=False, commit=False):
    with db_pool.get_connection() as conn:
        with conn.cursor() as db_cursor:
            db_cursor.execute(query, params)
            result = db_cursor.fetchone() if fetchone else None
        if commit:
            conn.commit()
    return result


def gen_rsa(id, email):
    jti = str(uuid.uuid4())
    issued_at = datetime.utcnow()
    expiration_time = issued_at + timedelta(minutes=120)

    claims = {
        'sub': 'fuel-emsi',
        'iss': 'http://127.0.0.1:5000',
        'iat': issued_at,
        'exp': expiration_time,
        'jti': jti,
        'name': email,
        'id':id,
        'roles': ['user']
    }

    token = jwt.encode(claims, private_key, algorithm='RS256')
    return token


def gen_refresh_token():
    return str(uuid.uuid4())



INVALID_CREDENTIALS_MSG = 'Invalid credentials'

@app.route('/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        email = data.get('email')
        password = data.get('password')

        if email and password:
            user = execute_query(login_query, (email,), fetchone=True)

            if user:
                stored_password = user[1]
                user_id = user[0]

                if stored_password and bcrypt.checkpw(password.encode('utf-8'), stored_password.encode('utf-8')):
                    access_token = gen_rsa(user_id, email)
                    refresh_token = gen_refresh_token()
                    print(f'Refresh Token for {email}: {refresh_token}')

                    # Use the constant for the error message
                    return jsonify({'access_token': access_token, 'refresh_token': refresh_token})
                else:
                    return jsonify({'message': INVALID_CREDENTIALS_MSG}), 401
            else:
                return jsonify({'message': INVALID_CREDENTIALS_MSG}), 401
        else:
            return jsonify({'message': INVALID_CREDENTIALS_MSG}), 401
    except Exception as e:
        print(f'Error executing login query: {e}')
        db_pool.rollback()
        return jsonify({'message': f'Error occurred while processing the request: {e}'}), 500




@app.route('/register', methods=['POST'])
def signup():
    try:
        data = request.get_json()
        email = data.get('email')
        nom = data.get('nom')
        password = data.get('password')
        prenom = data.get('prenom')

        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

        execute_query(signup_query, (email, nom, hashed_password, prenom), commit=True)
        user_id = execute_query(signup_id_query, (email,), fetchone=True)[0]

        access_token = gen_rsa(user_id, email)
        refresh_token = gen_refresh_token()
        print(f'Refresh Token for {email}: {refresh_token}')

        return jsonify({'message': 'Signup successful', 'access_token': access_token, 'refresh_token': refresh_token}), 200
    except Exception as e:
        print('Error executing signup query:', e)
        db_pool.rollback()
        return jsonify({'message': 'Error occurred while processing the request'}), 500



@app.route('/prices', methods=['GET'])
def fuel_prices():
    response = requests.get('https://total.smarteez.eu/submit/?station=189069')

    if response.status_code == 200:
        data = response.json()
        diesel_price = data['prix']['prix_diesel']
        gasoline_price = data['prix']['prix_essence']
        additive_price = data['prix']['prix_aditive']

        return jsonify({
            "Gasoil": float(diesel_price),
            "SansPlomb": float(gasoline_price),
            "Excellium": float(additive_price)
        })
    else:
        return jsonify({"error": f"Failed to retrieve data. Status code: {response.status_code}"})


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=rest_port, debug=True)
