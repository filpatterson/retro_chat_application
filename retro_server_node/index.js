//  enabling modules required for server work
var express = require('express');
var bodyParser = require('body-parser');
const mongoose = require('mongoose');
var Pusher = require('pusher');

//  enable application itself after setting all modules
var app = express();

//  set type of data that will be transmitted via server
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

//  setting paramters of connection via Pusher module
var pusher = new Pusher({
    appId: '974671',
    key: '8b63e6c6448bbe805172',
    secret: '30ef11caf55aac4c9c6d',
    cluster: 'eu'
});

//  connect to the database of MongoDB
mongoose.connect('mongodb://127.0.0.1/db', { useNewUrlParser: true });

//  create User scheme setting type of saved data (name of user and its password)
const Schema = mongoose.Schema;
const userSchema = new Schema({
    name: { type: String, required: true, },
    password: { type: String, required: true, },
    count: {type: Number}
});
var User = mongoose.model('User', userSchema);

//  save new user instance in database
userSchema.pre('save', function(next) {
    if (this.isNew) {
        User.count().then(res => {
            this.count = res; // Increment count
            next();
        });
    } else {
        next();
    }
});

module.exports = User;
var currentUser;

//  handling login action from client
app.post('/login', (req, res) => {
    //  make reference to the table of users
    const myModel = mongoose.model('User');
    
    //  search for user with such name as is in incoming request
    myModel.findOne({ name: req.body.name }, function (err, user) {
        //  if there is error connecting to database, then inform client about it
        if (err) {
            res.send("Error connecting to database");
        }

        //  if there is such user registered in system, then start checking his credentials
        if (user) {
            currentUser = user;
            if(currentUser.password === req.body.password) {
                //  send back user credentials for working inside system
                res.status(200).send(user)
                console.log(res)
            } else {
                res.send(400)
            }

        //  if there is no such user, then register new one
        } else {
            var newuser = new User({
                name: req.body.name,
                password: req.body.password
            });
            newuser.save(function(err) {
                if (err) throw err;
                console.log('User saved successfully!');
            });
            
            //  send back credentials of new user for working inside system
            currentUser = newuser;
            res.status(200).send(newuser)
        }
    });

})

// get list of all users in system
app.get('/users', (req, res) => {
    User.find({}, function(err, users) {
        if (err) throw err;
        
        //  send list of all user's inside system to the client
        res.send(users);
      });
})

// show user's presence in the chat
app.post('/pusher/auth/presence', (req, res) => {
    var socketId = req.body.socket_id;
    var channel = req.body.channel_name;
    var presenceData = {
        user_id: currentUser._id,
        user_info: {count: currentUser.count, name: currentUser.name}
    };

    res.send(pusher.authenticate(socketId, channel, presenceData));
});

//  create user communication via private channel setting its name
app.post('/pusher/auth/private', (req, res) => {
    res.send(pusher.authenticate(req.body.socket_id, req.body.channel_name));
});

//  receive message from client via private channel
app.post('/send-message', (req, res) => {
    pusher.trigger(req.body.channel_name, 'new-message', {
        message: req.body.message,
        sender_id: req.body.sender_id,
        message_time: req.body.message_time,
        sender_name: req.body.sender_name
    });
    res.send(200);
    console.log(res);
});

var port = process.env.PORT || 5000;
app.listen(port);