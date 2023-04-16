<h1 align="center">  KrishiMitra API</h1>
<h1 align="center"><img src = "https://github.com/0xSushmanth/Krishi-Mitra-API/blob/master/Krishi%20Mitra.png"></h1>
Krishi-Mitra-AI is API implementation for KRISHI MITRA APP predicting crop types based on soil and weather parameters such as nitrogen (N), phosphorous (P), potassium (K) levels in soil, temperature, humidity, pH, and rainfall.
This repository contains the code for the FastAPI backend of the Krishi-Mitra-ML application.

## Getting Started

### Prerequisites
Before deploying the API, make sure you have the following:

1. Python 3.9 or higher installed on your local machine.
2. Google Cloud Platform (GCP) account with appropriate permissions to deploy to App Engine.
3. Postman or any other REST client for testing the API endpoints.


## Installation
To install the necessary dependencies for the Krishi-Mitra-ML backend, you can run the following command:

1. Clone the repository to your local machine:
```sh
pip install -r requirements.txt
``` 

### Usage
Running the FastAPI Application Locally
To run the FastAPI application locally on your machine, you can use the following command:

```sh
uvicorn main:app --reload
```

This will start the FastAPI application on the localhost,
and you can access the API endpoints at http://127.0.0.1:8000 in your browser or through a REST client.

## Testing with Postman

You can test the FastAPI using Postman, a popular API client. Follow these steps

1. Launch Postman or any other REST client on your machine.

2. Set the HTTP method to POST or GET, depending on the endpoint you want to test.

3. Enter the URL of the deployed Krishi-Mitra-ML backend API endpoint. For example, if you have deployed the application on Google App Engine, the URL may look like 
```sh
https://<your-app-id>.appspot.com/predict.
```
4. Set the request body parameters in the Body section of the Postman request. You can either use form-data or raw JSON format, depending on the input expected by your FastAPI application. 
Here's an example of the expected input format for the API backend:

5. For form-data:
```sh
N: ratio of Nitrogen content in soil
P: ratio of Phosphorous content in soil
K: ratio of Potassium content in soil
temperature: temperature in degree Celsius
humidity: relative humidity in %
ph: pH value of the soil
rainfall: rainfall in mm
```
For raw JSON:
```sh
{
  "N": 80,
  "P": 100,
  "K": 60,
  "temperature": 30,
  "humidity": 70,
  "ph": 6.5,
  "rainfall": 800
}
```
6. Click on the Send button in Postman to send the request to the API backend.

7. The backend will process the request and return the predicted crop type as a response. You can view the response in the Response section of Postman.

8. Repeat the process with different input parameters to test different scenarios.

9. That's it! You can now use Postman to test the Krishi-Mitra-ML backend API endpoints and verify the functionality of the deployed FastAPI application.


## Deployment on Google Cloud Platform and AMD instance
Deploying the FastAPI Application to Google App Engine
To deploy the Krishi-Mitra-ML backend to Google App Engine, you can follow these steps:

1. Create an app.yaml file in the root directory of the project. You can use the provided app.yaml example in this repository or customize it based on your requirements.

2. Deploy the FastAPI application to App Engine using the gcloud command-line tool:
```sh
gcloud app deploy 
```
3. Once the deployment is complete, you can access the API endpoints at the provided App Engine URL.

## Documentation
Documentation is avaliable here at [API](https://krishimitra-0102.ue.r.appspot.com/docs)

## Contributing

If you would like to contribute to Crop Predictor, feel free to submit pull requests or open issues on the repository.
