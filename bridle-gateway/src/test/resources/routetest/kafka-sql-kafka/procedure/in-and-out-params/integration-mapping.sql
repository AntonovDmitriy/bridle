select_person(
  VARCHAR ${body['person']['first_name']},
  VARCHAR ${body['person']['last_name']},
  OUT INTEGER personAgeHeader,
  OUT VARCHAR personCityHeader,
  OUT VARCHAR personOccupationHeader
)
