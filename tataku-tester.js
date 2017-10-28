#!/usr/bin/env node
'use strict'

const http = require('http')

/**
 * This script simply fires continuous "tataku"
 * requests at the ESP32 HTTP server as a means
 * of testing
 *
 * Hint: If you call `chmod +x tataku-tester.js`
 * you'll be able to run it simply by typing its
 * name (no need to prefix node)
 */
function tataku() {
  const options = {
    host: '192.168.4.1',
    path: '/'
  }

  const callback = response => {
    let str = ''

    response.on('data', function(chunk) {
      str += chunk
    })

    response.on('end', function() {
      console.log(str)
    })
  }

  http.request(options, callback).end()
}

const bpm = 80
const quarterNote = 60 / bpm * 1000

// Send a request every 1000 ms
setInterval(() => {
  tataku()
}, quarterNote)

// Send another request that comes at every fourth
setTimeout(() => {
  setInterval(() => {
    tataku()
  }, quarterNote * 4)
}, quarterNote / 2)
