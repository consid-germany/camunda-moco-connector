## Mountebank Stub for Moco

Mountebank is RERST API mocking framework which answers calls to it with realistic answers. To achieve this you can 
set up an imposter under the path of the API. 

* start the mountebank container in your docker environment
* use a REST client of your choosing
* POST to `http://localhost:2525/imposters` with the `subs.json` as JSON payload
* DELETE to `http://localhost:2525/imposters` to delete all imposters
* Check `http://localhost:2525` in your browser, if your imposter is there