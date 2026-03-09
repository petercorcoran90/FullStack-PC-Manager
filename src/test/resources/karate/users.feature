Feature: App User API Tests
  To test the authentication and registration endpoints

Background: Setup the Base path
  Given url baseUrl

Scenario: Register a new user successfully
  Given path '/api/users/register'
  And header Accept = 'application/json'
  And request { "username": "karateuser", "password": "password123", "role": "ROLE_USER" }
  When method post
  Then status 201
  And match response == 'User registered successfully'

Scenario: Login user successfully
  # 1. Register a user dynamically to ensure they exist in the DB for this test
  Given path '/api/users/register'
  And header Accept = 'application/json'
  And request { "username": "loginuser", "password": "password", "role": "ROLE_USER" }
  When method post

  # 2. Test the login endpoint
  Given path '/api/users/login'
  And header Accept = 'application/json'
  And request { "username": "loginuser", "password": "password" }
  When method post
  Then status 200
  And match response.message == 'Login Successful'
  And match response.token == '#present'
  And match response.token == '#string'

Scenario: Login failure due to bad credentials
  Given path '/api/users/login'
  And header Accept = 'application/json'
  And request { "username": "wronguser", "password": "wrongpass" }
  When method post
  Then status 401
  And match response == 'Invalid username or password.'