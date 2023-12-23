import asyncio
from flask import Flask, jsonify
import requests
import py_eureka_client.eureka_client as eureka_client

rest_port = 8070

# Eureka server URL
eureka_server_url = "http://localhost:8010/eureka"



your_rest_server_port = 8070
# The flowing code will register your server to eureka server and also start to send heartbeat every 30 seconds
eureka_client.init(eureka_server=eureka_server_url,
                   app_name="fuel-flask",
                   instance_host="localhost",
                   instance_port=your_rest_server_port)



app = Flask(__name__)

# https://www.tomobila.ma/prix-diesel-maroc-aujourd-hui/

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
    app.run(host="localhost",port=rest_port,debug=True)
