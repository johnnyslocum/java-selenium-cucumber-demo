@ApiTest

Feature: Karate Demo

  Background:
    * def baseUrl = 'https://jsonplaceholder.typicode.com'
    * url baseUrl

  Scenario: Retrieve user by ID
    Given path 'users', 1
    When method GET
    Then status 200
    And match $.id == 1
    * eval
    """
    var pretty = karate.pretty(response);
    karate.log(pretty);
    """


  Scenario: Lenient validation (handles dynamic headers, validates body)
    Given path 'users', 1
    When method get
    Then status 200

    # Headers - presence/pattern check for values that can vary
    And match header Content-Type contains 'application/json'
    And match header Transfer-Encoding == 'chunked'
    And match header Cache-Control contains 'max-age'
    And match header ETag != null
    And match header Server contains 'cloudflare'
    And match header Age == '#regex \\d+'

    # Body - assert key values, check numeric-strings via regex
    And match response.id == 1
    And match response.name == 'Leanne Graham'
    And match response.username == 'Bret'
    And match response.email == 'Sincere@april.biz'
    And match response.address.city == 'Gwenborough'
    And match response.address.geo.lat == '#regex -?\\d+\\.\\d+'
    And match response.address.geo.lng == '#regex -?\\d+\\.\\d+'
    And match response.company.name == 'Romaguera-Crona'
    * eval
    """
    var pretty = karate.pretty(response);
    karate.log(pretty);
    """


  # Scenario checking exact match against an expected JSON file
  Scenario: Full response equals stored expected JSON (strict)
    Given path 'users', 1
    When method get
    Then status 200

    # load expected body from json file and compare
    * def expected = read('classpath:expectedresponses/expected-user-1.json')
    And match response == expected
    * eval
    """
    var pretty = karate.pretty(response);
    karate.log(pretty);
    """


  # Data-driven version of the above, with an optional full response match if a snapshot file is present for the given ID
  Scenario Outline: Validate user <id> (load per-id expected snapshot if present)
    Given path 'users', <id>
    When method get
    Then status 200

    # quick field assertions always checked
    And match response.id == <id>
    And match response.name == '<name>'
    And match response.company.name == '<company>'

    # Optionally attempt to load a full expected snapshot file named expected-user-<id>.json
    * def expectedFile = 'classpath:expectedresponses/expected-user-' + <id> + '.json'
    * eval
      """      try {
        var expected = karate.read(expectedFile);
        karate.log('Found expected file for id:', <id>);
        karate.match(response, expected);
        var pretty = karate.pretty(response);
        karate.log(pretty);
      } catch (e) {
        karate.log('No expected snapshot file for id:', <id>, ' — using field assertions only');
      }
      """
    Examples:
      | id | name             | company            |
      | 1  | Leanne Graham    | Romaguera-Crona    |
      | 2  | Ervin Howell     | Deckow-Crist       |
      | 3  | Clementine Bauch | Romaguera-Jacobson |