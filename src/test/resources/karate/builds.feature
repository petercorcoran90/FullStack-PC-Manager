Feature: PC Build Management API Tests
  To verify build creation, part management, and total cost calculation

Background:
  Given url baseUrl
  
  # 1. Setup: Register and Login a standard user
  * def testUsername = 'build_user_' + java.util.UUID.randomUUID().toString().substring(0,8)
  Given path '/api/users/register'
  And request { "username": '#(testUsername)', "password": "password", "role": "ROLE_USER" }
  When method post
  Then status 201

  Given path '/api/users/login'
  And request { "username": '#(testUsername)', "password": "password" }
  When method post
  Then status 200
  * def userAuthToken = response.token
  * def authHeader = { Authorization: '#("Bearer " + userAuthToken)', Accept: 'application/json' }

  # 2. Setup: Create a Hardware Part as Admin to use in the builds
  # (Login as the admin created by your DataInitialiser)
  Given path '/api/users/login'
  And request { "username": "admin", "password": "admin" }
  When method post
  Then status 200
  * def adminToken = response.token

  Given path '/api/parts'
  And header Authorization = 'Bearer ' + adminToken
  And request { "name": "Build RAM", "manufacturer": "Corsair", "category": "RAM", "price": 80.00, "stockLevel": 20 }
  When method post
  Then status 201
  * def partId = response.id

Scenario: Full PC Build Lifecycle (Acceptance Criteria 1-4)
  # AC1: Creating a New Build Profile
  Given path '/api/builds'
  And configure headers = authHeader
  And param username = testUsername
  And param name = 'Budget Gaming 2026'
  When method post
  Then status 201
  And match response.buildName == 'Budget Gaming 2026'
  * def buildId = response.id

  # AC2 & AC4: Adding Multiple Parts and Duplicate Part Addition
  # Add the first RAM stick
  Given path '/api/builds/' + buildId + '/parts/' + partId
  And configure headers = authHeader
  When method post
  Then status 200
  And match response.parts == '#[1]'
  And match response.totalPrice == 80.00

  # Add a second identical RAM stick
  Given path '/api/builds/' + buildId + '/parts/' + partId
  And configure headers = authHeader
  When method post
  Then status 200
  And match response.parts == '#[2]'
  And match response.totalPrice == 160.00

  # AC3: Removing a Part from a Build
  # Remove one instance of the RAM
  Given path '/api/builds/' + buildId + '/parts/' + partId
  And configure headers = authHeader
  When method delete
  Then status 200
  And match response.parts == '#[1]'
  And match response.totalPrice == 80.00

  # Verify build appears in user's build list
  Given path '/api/builds/user/' + testUsername
  And configure headers = authHeader
  When method get
  Then status 200
  And match response[0].buildName == 'Budget Gaming 2026'