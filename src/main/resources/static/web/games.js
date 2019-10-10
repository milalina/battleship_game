new Vue({
    el: "#games",
    data: {
        games: [],
        gamePlayersArray: [],
        uniquePlayerIdArray: [],
        leaderboard: [],
        emptyArray: [],
        email: null,
        password: null,
        feedback: null,
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
                    this.games = data;
                    console.log(this.games)
                    this.isLoaded();
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
        },

        fillUpGamePlayersArray(gamePlayersArray, games) {
            for (i in this.games) {
                this.gamePlayersArray.push.apply(this.gamePlayersArray, this.games[i].gamePlayers)
            }
            console.log(this.gamePlayersArray)
            this.fillUpUniquePlayerIdArray();
        },

        fillUpUniquePlayerIdArray() {
            var emptyIntermediatePlayerIdArray = [];
            for (i in this.gamePlayersArray) {
                emptyIntermediatePlayerIdArray.push(this.gamePlayersArray[i].player.id)
            }
            this.uniquePlayerIdArray = emptyIntermediatePlayerIdArray.filter((item, index) => emptyIntermediatePlayerIdArray.indexOf(item) === index);
            this.fillUpObjectLeaderboard();
        },

        fillUpObjectLeaderboard() {
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

        signup(){
            if(this.email ){
                this.feedback = "Well done!"
            }else{
                this.feedback = "You must enter all fields"
                console.log(this.email)
            }
        },

        loggedI(){

        }


    },

    /* computed: {
        leaderboardComplete: function () {
            for (i in this.leaderboard) {
                if (this.leaderboard[i].won == undefined) {
                    this.leaderboard[i].won = 0
                } else if (this.leaderboard[i].lost == undefined) {
                    this.leaderboard[i].lost = 0
                } else if (this.leaderboard[i].tied == undefined) {
                    this.leaderboard[i].tied = 0
                }
            }
        }
    }, */

    created: function () {
        this.fetchData();
    },

})

/* 
fillUpObjectLeaderboard() {
            console.log(this.uniquePlayerIdArray);
            console.log(this.gamePlayersArray)
            var total=[];
            for (i in this.uniquePlayerIdArray) {
                var won;
                var lost;
                var tied;
                this.emptyArray=[];
                total=[];
                for (j in this.gamePlayersArray) {
                    if (this.uniquePlayerIdArray[i] == this.gamePlayersArray[j].player.id && this.gamePlayersArray[j].score!=null) {
                        this.emptyArray.push(this.gamePlayersArray[j].score)
                    }  
                }
                console.log(this.emptyArray)
                total.push(this.emptyArray.reduce((a, b) => a + b));
                console.log(total) 
            }
        }
*/