new Vue({
    el: "#games",
    data: {
        games: [],
        gamesObjectForHTML: [],
        gamePlayersArray: [],
        uniquePlayerIdArray: [],
        leaderboard: [],
        emptyArray: [],
        email: null,
        password: null,
        feedback: null,
        login: true,
        logout: false,
        currentPlayer: null,
        feedbackGameCreated: null,
    },
    methods: {
        fetchData: function () {
            fetch("http://localhost:8080/api/games")
                .then(function (response) {
                    console.log(response);
                    return response.json();
                })
                .then((data) => {
                    console.log(data);
                    this.games = data.games;
                    this.currentPlayer = data.player;
                    if (data.player) {
                        this.email = data.player.name
                    }
                    console.log(this.games)
                    this.isLoaded();
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        fetchAuthenticationInfo: function () {
            fetch("http://localhost:8080/api/login?username=" + this.email + "&password=" + this.password, {
                    method: "post"
                })
                .then(function (response) {
                    console.log(response);
                    return response.status

                })
                .then((status) => {
                    if (status == 200) {
                        this.login = false;
                        this.logout = true;
                        this.feedback = "Well done!"
                    } else {
                        this.feedback = "Try again!"
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
        },
        createNewGame: function () {
            fetch("http://localhost:8080/api/game?username=" + this.email, {
                    method: "post"
                })
                .then(function (response) {
                    console.log(response);
                    return response.status

                })
                .then((status) => {
                    if (status == 201) {
                     this.feedbackGameCreated="Game created!"
                     setTimeout(() => {
                        this.feedbackGameCreated = null;
                    }, 1000);
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        joinGame: function () {
            fetch("http://localhost:8080/api/game?username=" + this.email, {
                    method: "post"
                })
                .then(function (response) {
                    console.log(response);
                    return response.status

                })
                .then((status) => {
                    if (status == 201) {
                     this.feedbackGameCreated="Game created!"
                     setTimeout(() => {
                        this.feedbackGameCreated = null;
                    }, 1000);
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        fetchLogoutInfo: function () {
            fetch("http://localhost:8080/api/logout?username=" + this.email + "&password=" + this.password, {
                    method: "post"
                })
                .then(function (response) {
                    console.log(response);
                    return response.status

                })
                .then((status) => {
                    if (status == 200) {
                        this.login = true;
                        this.logout = false;
                        this.email = null;
                        this.password = null;
                        this.feedback = null;
                        this.currentPlayer = null;
                    } else {
                        this.feedback = "Try again!"
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        isLoaded: function () {
            if (this.games.length == 0) {
                return false;
            } else {
                this.milisecsToLocalTime();
                this.fillUpGamePlayersArray();
                return true
            }
        },

        milisecsToLocalTime: function () {
            for (var i in this.games) {
                var convertingTime = new Date(this.games[i].created);
                this.games[i].created = convertingTime.toLocaleString();
            }
            this.newObjectForDisplayingGames();
        },

        newObjectForDisplayingGames: function () {
            for (i in this.games) {
                var player2Email = ""
                var gp2ID = ""
                var feedback;
                var htmlGameUrl;
                if (this.games[i].gamePlayers[1]) {
                    player2Email = this.games[i].gamePlayers[1].player.email
                    gp2ID = this.games[i].gamePlayers[1].id
                }
                if (this.games[i].gamePlayers[1] && this.games[i].gamePlayers[0].score != null) {
                    feedback = "Game Over!"
                }
                if (this.games[i].gamePlayers[1] && this.games[i].gamePlayers[0].score == null) {
                    feedback = "Game in Progress!"
                }
                if (!this.games[i].gamePlayers[1]) {
                    feedback = "Join the Game!"
                }
                if (this.currentPlayer && this.currentPlayer.name == this.games[i].gamePlayers[0].player.email) {
                    htmlGameUrl = "game.html?gp=" + this.games[i].gamePlayers[0].id
                    console.log(htmlGameUrl )
                } else if (this.currentPlayer && gp2ID > 0 &&  this.currentPlayer.name == this.games[i].gamePlayers[1].player.email) {
                    htmlGameUrl = "game.html?gp=" + gp2ID
                    console.log(htmlGameUrl)
                }else{htmlGameUrl = "games.html"}

            
            this.gamesObjectForHTML.push({
                "started": " " + this.games[i].created,
                "gamePlayer1": " " + this.games[i].gamePlayers[0].player.email,
                "gamePlayer2": " " + player2Email,
                "statusFeedback": " " + feedback,
                "gameId": " " + this.games[i].id,
                "url": " " + htmlGameUrl,
            })
            console.log(this.gamesObjectForHTML)
            }
    },
    //put all gamePlayers in one array
    fillUpGamePlayersArray(gamePlayersArray, games) {
        for (i in this.games) {
            this.gamePlayersArray.push.apply(this.gamePlayersArray, this.games[i].gamePlayers)
        }
        console.log(this.gamePlayersArray)
        this.fillUpUniquePlayerIdArray();
    },
    //reduce all gamePlayers to players
    fillUpUniquePlayerIdArray() {
        var emptyIntermediatePlayerIdArray = [];
        for (i in this.gamePlayersArray) {
            emptyIntermediatePlayerIdArray.push(this.gamePlayersArray[i].player.id)
        }
        this.uniquePlayerIdArray = emptyIntermediatePlayerIdArray.filter((item, index) => emptyIntermediatePlayerIdArray.indexOf(item) === index);
        this.fillUpObjectLeaderboard();
    },

    fillUpObjectLeaderboard() {
        console.log("test")
        var total = [];
        for (i in this.uniquePlayerIdArray) {
            var player;
            var total;
            var won;
            var lost;
            var tied;
            this.emptyArray = [];
            totalArray = [];
            for (j in this.gamePlayersArray) {
                if (this.uniquePlayerIdArray[i] == this.gamePlayersArray[j].player.id && this.gamePlayersArray[j].score != null) {
                    player = this.gamePlayersArray[j].player.email
                    this.emptyArray.push(this.gamePlayersArray[j].score)
                }
            }
            totalArray.push(this.emptyArray.reduce((a, b) => a + b));

            //counting the occurrences of won, lost or tie in a player's array of scores
            total = totalArray[0];
            var counts = {};
            for (k in this.emptyArray) {
                var num = this.emptyArray[k];
                counts[num] = counts[num] ? counts[num] + 1 : 1;
            }
            won = counts[1.0] ? counts[1.0] : 0;
            lost = counts[0] ? counts[0] : 0;
            tied = counts[0.5] ? counts[0.5] : 0;

            //dynamically filling up the object leaderboard
            this.leaderboard.push({
                "name": " " + player,
                "total": " " + total,
                "won": " " + won,
                "lost": " " + lost,
                "tied": " " + tied,
            })
        }
    },

    signup() {
        if (this.email && this.passowrd) {
            this.login = false;
            this.logout = true;
        } else {
            this.feedback = "You must enter all fields"
        }
    },

    doLogin() {
        console.log("login function")
        if (this.email && this.passowrd) {
            this.login = false;
            this.logout = true;
        } else {
            this.feedback = "You must enter all fields"
        }
    },


},
created: function () {
    this.fetchData();
},

})