merge_person(
  INOUT INTEGER ${body['person']['id']} id,
  INOUT VARCHAR ${body['person']['first_name']} first_name,
  INOUT VARCHAR ${body['person']['last_name']} last_name,
  INOUT INTEGER ${body['person']['age']} age,
  INOUT VARCHAR ${body['person']['city']} city,
  INOUT VARCHAR ${body['person']['occupation']} occupation
)