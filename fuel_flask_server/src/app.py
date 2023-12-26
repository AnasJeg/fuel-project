import os
import uuid
from datetime import datetime, timedelta
import bcrypt
from flask import Flask, jsonify, request
import mysql.connector
import requests
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
import jwt
import py_eureka_client.eureka_client as eureka_client

eureka_server_url = "http://localhost:8010/eureka"


private_key_path = os.path.join(os.path.dirname(__file__), 'private.pem')
private_key = serialization.load_pem_private_key(open(private_key_path, 'rb').read(), password=None)

conn = None

def db_connection():
    try:
        conn = mysql.connector.connect(
            host=os.environ.get("DATABASE_HOST", "localhost"),
            port=os.environ.get("DATABASE_PORT", "3306"),
            database=os.environ.get("DATABASE_NAME", "fuel"),
            user=os.environ.get("DATABASE_USER", "root"),
            password=os.environ.get("DATABASE_PASS", "")
        )
        print("Connected to the database successfully")
        return conn
    except Exception as e:
        print("Error connecting to the database:", e)
        return None

def create_db_connection():
    global conn
    try:
        if conn is None or conn.is_closed():
            conn = db_connection()
        db_cursor = conn.cursor()
        print("Connected to the database successfully")
        return conn, db_cursor
    except Exception as e:
        print('Error creating database cursor', e)
        return None, None

conn, db_cursor = create_db_connection()


def gen_rsa(username):
    jti = str(uuid.uuid4())
    issued_at = datetime.utcnow()
    expiration_time = issued_at + timedelta(minutes=30)

    claims = {
        'sub': 'emsi',
        'iss': 'http://127.0.0.1:5000',
        'iat': issued_at,
        'exp': expiration_time,
        'jti': jti,
        'name': username,
        'roles': ['user']
    }

    token = jwt.encode(claims, private_key, algorithm='RS256')
    return token

def gen_refresh_token():
    return str(uuid.uuid4())


app = Flask(__name__)


rest_port = 8070
eureka_client.init(
    eureka_server=eureka_server_url,
    app_name="fuel-flask",
    instance_host="localhost",
    instance_port=rest_port
)

login_query = "SELECT password FROM user WHERE email = %s"

@app.route('/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        email = data.get('email')
        password = data.get('password')

        if email is not None and password is not None:
            db_cursor.execute(login_query, (email,))
            user = db_cursor.fetchone()

            if user:
                stored_password = user[0]

                if stored_password:
                    if bcrypt.checkpw(password.encode('utf-8'), stored_password.encode('utf-8')):
                        jti = str(uuid.uuid4())
                        access_token = gen_rsa(email)
                        refresh_token = gen_refresh_token()
                        print(f'Refresh Token for {email}: {refresh_token}')

                        return jsonify({'access_token': access_token, 'refresh_token': refresh_token})
                    else:
                        return jsonify({'message': 'Invalid credentials'}), 401
                else:
                    return jsonify({'message': 'Invalid credentials'}), 401
            else:
                return jsonify({'message': 'Invalid credentials'}), 401
        else:
            return jsonify({'message': 'Invalid credentials'}), 401
    except Exception as e:
        print(f'Error executing login query: {e}')
        return jsonify({'message': f'Error occurred while processing the request: {e}'}), 500

signup_query = "INSERT INTO user (city, email, nom, password, prenom) VALUES (%s, %s, %s, %s, %s)"

@app.route('/register', methods=['POST'])
def signup():
    try:
        data = request.get_json()
        city = data.get('city')
        email = data.get('email')
        nom = data.get('nom')
        password = data.get('password')
        prenom = data.get('prenom')

        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

        db_cursor.execute(signup_query, (city, email, nom, hashed_password, prenom))
        conn.commit()

        
        access_token = gen_rsa(email)
        refresh_token = gen_refresh_token()

        
        
        print(f'Refresh Token for {email}: {refresh_token}')

        return jsonify({'message': 'Signup successful', 'access_token': access_token, 'refresh_token': refresh_token}), 200
    except Exception as e:
        print('Error executing signup query:', e)
        conn.rollback()
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
    app.run(host="localhost", port=rest_port, debug=True)
