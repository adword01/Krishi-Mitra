from flask import Flask, request, jsonify
import numpy as np
import pickle

model = pickle.load(open('RandomForest.pkl', 'rb'))
print(1)

app = Flask(__name__)


@app.route('/')
def index():
    return "Try to use /predict endpoint"


#
@app.route('/predict', methods=['GET', 'POST'])
def predict():
    if request.method == 'GET':
        # Handle GET request
        # Access query parameters using request.args
        N = request.args.get('N')
        P = request.args.get('P')
        K = request.args.get('K')
        temperature = request.args.get('temperature')
        humidity = request.args.get('humidity')
        ph = request.args.get('ph')
        rainfall = request.args.get('rainfall')

    elif request.method == 'POST':
        # Handle POST request
        N = request.form.get('N')
        P = request.form.get('P')
        K = request.form.get('K')

        temperature = request.form.get('temperature')
        humidity = request.form.get('humidity')
        ph = request.form.get('ph')
        rainfall = request.form.get('rainfall')

    else:
        # Handle other HTTP methods if needed
        return jsonify({'error': 'Invalid HTTP method'})

    # Perform prediction
    input_query = np.array([[N, P, K, temperature, humidity, ph, rainfall]])
    result = model.predict(input_query)[0]

    # Customize the JSON response
    response = {'crop': str(result)}
    return jsonify(response)


if __name__ == '__main__':
    app.run(debug=True)
