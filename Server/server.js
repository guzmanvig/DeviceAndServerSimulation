const express = require('express')
const app = express()
const port = 3000

app.use(express.json());

app.post('/alert', (req, res) => {
    var device_id = req.body.device
    var particle_size = req.body.size
    var measurement = req.body.measurement
    var date = req.body.date
    var log_message = "Recevied RED alert from device " + device_id + 
                        ". Particle of size " + particle_size + " measured " + measurement +
                        " at " + date
    console.log(log_message);
    res.send('Alert received!')
})

app.post('/measurement', (req, res) => {
    var device_id = req.body.device
    var date = req.body.date
    var pm1 = req.body.pm1
    var pm2 = req.body.pm2
    var pm4 = req.body.pm4
    var pm10 = req.body.pm5
    sendToDB(device_id, date, pm1, pm2, pm4, pm10)
    res.send('Measure received!')
})

app.listen(port, () => {
  console.log(`Server listening at http://localhost:${port}`)
})


function sendToDB(device_id, date, pm1, pm2, pm4, pm10) {
 console.log('Data sent')
}