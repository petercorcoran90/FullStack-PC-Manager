Feature: App User API Tests
  To test the authentication and registration endpoints

Background: Setup Base Path and Test User
  Given url baseUrl
  
  # Register a standard user before every test so the login scenarios have an account to use
  * def testUser = 'user_' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/users/register'
  And header Accept = 'application/json'
  And request { "username": '#(testUser)', "password": "password", "role": "ROLE_USER" }
  When method post

# --- SCENARIOS ---

Scenario: Register a completely new user successfully
  * def newUser = 'new_' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/users/register'
  And header Accept = 'application/json'
  And request { "username": '#(newUser)', "password": "password123", "role": "ROLE_USER" }
  When method post
  Then status 201
  And match response == 'User registered successfully'

Scenario: Login successfully with valid credentials
  Given path '/api/users/login'
  And header Accept = 'application/json'
  And request { "username": '#(testUser)', "password": "password" }
  When method post
  Then status 200
  And match response.message == 'Login Successful'
  And match response.token == '#string'

Scenario: Login failure due to bad credentials
  Given path '/api/users/login'
  And header Accept = 'application/json'
  And request { "username": "wronguser", "password": "wrongpass" }
  When method post
  Then status 401
  And match response == 'Invalid username or password.'