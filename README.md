<h1 align="center">  KrishiMitra API</h1>


This repository is API implementation for [KRISHI MITRA-ML](https://github.com/adword01/Krishi-Mitra/tree/ML) , predicts the suitable crop based on input data such as nitrogen (N), phosphorous (P), potassium (K) levels in soil, temperature, humidity, pH, and rainfall.

## Getting Started

These instructions will help you set up and run the Crop Predictor application locally for testing and development purposes.

### Prerequisites

- Python 3.x
- Virtual environment (e.g., `virtualenv` or `conda`)

### Installation

1. Clone the repository to your local machine:
```sh
git clone https://github.com/GeekyCats/Crop-Predictor.git
``` 
```sh
cd Crop-Predictor
```
# Using virtualenv
```sh
virtualenv venv
source venv/bin/activate
```
# Using conda
```sh
conda create --name venv
conda activate venv
pip install -r requirements.txt
```
```sh
flask --app application run
```
## Testing with Postman

You can test the FASTAPI using Postman, a popular API client. Follow these steps:

1. Start the FASTAPI application by running the `uvicorn application:app --reload` script.
2. Open Postman and create a new request.
3. Set the HTTP method to POST and enter the endpoint URL of the FASTAPI `/predict` endpoint (e.g., `http://127.0.0.1:5000/predict`).
4. Go to the "Body" tab in Postman, select "form-data" as the type of body, and enter the input data as key-value pairs.
5. Click on the "Send" button to send the POST request.
6. You should receive a JSON response from the API, containing the predicted crop based on the input data.

## Parameters to pass
  You need to enter following values to predict the crop: -
  - N (ratio of Nitrogen content in soil)
  - P (ratio of Phosphorous content in soil)
  - K (ratio of Potassium content in soil)
  - ph (ph value of soil)
  - temperature (temperature in degree Celsius)
  - humidity (relative humidity in %)
  - rainfall (rainfall in mm)

## Deployment on Google Cloud Platform

You can also deploy the Crop Predictor application on Google Cloud Platform (GCP) using tools like Google App Engine or Compute Engine. Follow the deployment steps specific to GCP in the [Deploying on GCP](#deploying-on-gcp) section.

## Contributing

If you would like to contribute to Crop Predictor, feel free to submit pull requests or open issues on the repository.
