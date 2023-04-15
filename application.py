from fastapi import FastAPI, Form, Request
from fastapi.responses import JSONResponse
import numpy as np
import pickle
import uvicorn

app = FastAPI()

# Load the trained model
model = pickle.load(open('RandomForest.pkl', 'rb'))

@app.get('/')
def index():
    return "Try to use /predict endpoint"

@app.post('/predict')
async def predict(request: Request,
                  N: float = Form(None, description="Ratio of Nitrogen content in soil"),
                  P: float = Form(None, description="Ratio of Phosphorous content in soil"),
                  K: float = Form(None, description="Ratio of Potassium content in soil"),
                  temperature: float = Form(None, description="Temperature in degree Celsius"),
                  humidity: float = Form(None, description="Relative humidity in %"),
                  ph: float = Form(None, description="pH value of the soil"),
                  rainfall: float = Form(None, description="Rainfall in mm")):
    """
    Endpoint to predict crop based on soil and weather parameters.

    Parameters:
    - N: Ratio of Nitrogen content in soil
    - P: Ratio of Phosphorous content in soil
    - K: Ratio of Potassium content in soil
    - temperature: Temperature in degree Celsius
    - humidity: Relative humidity in %
    - ph: pH value of the soil
    - rainfall: Rainfall in mm

    Note: Parameters can be provided in both form-data and JSON request body.
    """
    # Check if request body is in JSON format
    if request.headers.get("Content-Type") == "application/json":
        request_data = await request.json()
        N = request_data.get('N', N)
        P = request_data.get('P', P)
        K = request_data.get('K', K)
        temperature = request_data.get('temperature', temperature)
        humidity = request_data.get('humidity', humidity)
        ph = request_data.get('ph', ph)
        rainfall = request_data.get('rainfall', rainfall)

    # Perform prediction
    input_query = np.array([[N, P, K, temperature, humidity, ph, rainfall]])
    result = model.predict(input_query)[0]

    # Customize the JSON response
    response = {'crop': str(result)}
    return JSONResponse(content=response)

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8000)

