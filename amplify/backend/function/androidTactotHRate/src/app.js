/*
Copyright 2017 - 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at
    http://aws.amazon.com/apache2.0/
or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
*/


/* Amplify Params - DO NOT EDIT
	API_TACTHRATE_GRAPHQLAPIENDPOINTOUTPUT
	API_TACTHRATE_GRAPHQLAPIIDOUTPUT
	API_TACTHRATE_GRAPHQLAPIKEYOUTPUT
	ENV
	REGION
Amplify Params - DO NOT EDIT */
const axios = require('axios');
var express = require('express')
var bodyParser = require('body-parser')
var awsServerlessExpressMiddleware = require('aws-serverless-express/middleware')
const AWS = require('aws-sdk');
const graphqlQuery = require('./graphql.js').query;
const graphqlPost = require('./graphqlPush.js').mutation;
const gql = require('graphql-tag');
const graphql = require('graphql');
const AWSAppSyncClient = require('aws-appsync').default;
require('es6-promise').polyfill();
require('isomorphic-fetch');
const { print } = graphql;

// declare a new express app
var app = express()
app.use(bodyParser.json())
app.use(awsServerlessExpressMiddleware.eventContext())

// Enable CORS for all methods
app.use(function(req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header(
    'Access-Control-Allow-Headers',
    'Origin, X-Requested-With, Content-Type, Accept'
  );
  next();
});

const url = process.env.API_TACTHRATE_GRAPHQLAPIENDPOINTOUTPUT;
const region = process.env.REGION;

AWS.config.update({
  region,
  credentials: new AWS.Credentials(
    process.env.AWS_ACCESS_KEY_ID,
    process.env.AWS_SECRET_ACCESS_KEY,
    process.env.AWS_SESSION_TOKEN
  ),
});
const credentials = AWS.config.credentials;

const appsyncClient = new AWSAppSyncClient(
  {
    url,
    region,
    auth: {
      type: 'API.KEY',
      apiKey: 'da2-iksdqwxhwjctzdcpbejazjdnsm',
      credentials,
    },
    disableOffline: true,
  },
  {
    defaultOptions: {
      query: {
        fetchPolicy: 'network-only',
        errorPolicy: 'all',
      },
    },
  }
);

const query = gql(graphqlQuery);
const postM = gql(graphqlPost);


/**********************
 * Example get method *
 **********************/

app.get('/rate', async function(_, res) {
  try {
    const client = await appsyncClient.hydrated();
    const data = await client.query({ query });
    res.json({ success: 'get call succeeded!', data });
  } catch (error) {
    console.log(error);
    res.json({ error: 'get call failed!', error });
  }
});

app.get('/item/*', function(req, res) {
  // Add your code here
  res.json({success: 'get call succeed!', url: req.url});
});

/****************************
* Example post method *
****************************/

const createHRate = gql`
  mutation createHRate($input: CreateHRateInput!) {
    createHRate(input: $input) {
      currentRate
    }
  }
`

app.post('/pushRate', async function(req, res) {
  console.log('push received with req ' + req);
  
  //var newInput = JSON.parse(req.body);
  var newRateAr = req.body.rate;

  console.log('push event dot notation ' + newRateAr);
  //console.log('push event body parenth notation' + newInput);


  const rateDetails = {
    currentRate: newRateAr
  };

  const client = new AWSAppSyncClient(
    {
      url: "https://lf3texuvbva33ah4gyviecbgei.appsync-api.us-east-2.amazonaws.com/graphql",
      region: 'us-east-2',
      auth: {
        type: 'API.KEY',
        apiKey: 'da2-iksdqwxhwjctzdcpbejazjdnsm',
        credentials,
      },
      disableOffline: true,
    },
    {
      defaultOptions: {
        query: {
          fetchPolicy: 'network-only',
          errorPolicy: 'all',
        },
      },
    }
  );

  try {
    const graphqlData = await axios({
      url: "https://lf3texuvbva33ah4gyviecbgei.appsync-api.us-east-2.amazonaws.com/graphql",
      method: 'post',
      headers: {
        'x-api-key': "da2-iksdqwxhwjctzdcpbejazjdnsm"
      },
      data: {
        query: print(createHRate),
        variables: {
          input: {
            currentRate: newRateAr
          }
        }
      }
    });
    const body = {
      message: "successfully created HRate!"
    }
    res.json({success: JSON.stringify(body), url: req.url, body: req.body})
  } catch (err) {
    console.log('error creating todo: ', err);
  } 

});

app.post('/item/*', function(req, res) {
  // Add your code here
  res.json({success: 'post call succeed!', url: req.url, body: req.body})
});

/****************************
* Example put method *
****************************/

app.put('/item', function(req, res) {
  // Add your code here
  res.json({success: 'put call succeed!', url: req.url, body: req.body})
});

app.put('/item/*', function(req, res) {
  // Add your code here
  res.json({success: 'put call succeed!', url: req.url, body: req.body})
});

/****************************
* Example delete method *
****************************/

app.delete('/item', function(req, res) {
  // Add your code here
  res.json({success: 'delete call succeed!', url: req.url});
});

app.delete('/item/*', function(req, res) {
  // Add your code here
  res.json({success: 'delete call succeed!', url: req.url});
});

app.listen(3000, function() {
    console.log("App started")
});

// Export the app object. When executing the application local this does nothing. However,
// to port it to AWS Lambda we will create a wrapper around that will load the app from
// this file
module.exports = app
