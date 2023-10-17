insert_into_person(
  VARCHAR ${body['person']['first_name']},
  VARCHAR ${body['person']['last_name']},
  INTEGER ${body['person']['age']},
  VARCHAR ${body['person']['city']},
  VARCHAR ${body['person']['occupation']}
)