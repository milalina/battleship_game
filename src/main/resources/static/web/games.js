new Vue({
    el: "#games",
    data: {
        games: [],
        gamePlayersArray:[],
        leaderboard:[{}],
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

        fillUpGamePlayersArray(gamePlayersArray, games){
            for(i in this.games){
             this.gamePlayersArray.push.apply(this.gamePlayersArray, this.games[i].gamePlayers)
            } 
            console.log(this.gamePlayersArray)
        },

        fillUpObjectLeaderboard(){
            
            
        }

    },

    /*computed: {
        isLoaded: function () {
            if (this.games.length == 0) {
                return false;
            } else {
                return true
            }
        }
    },*/

    created: function () {
        this.fetchData();
    },

})