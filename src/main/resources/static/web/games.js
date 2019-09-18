new Vue({
    el: "#games",
    data: {
        games: [],
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
                    this.isLoaded();
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        isLoaded: function () {
            if (this.games.length == 0) {
                console.log("yes")
                return false;
            } else {
                this.milisecsToLocalTime();
                console.log("no")
                return true
            }
        },

        milisecsToLocalTime: function () {
            for (var i in this.games) {
                var convertingTime = new Date(this.games[i].created);
                this.games[i].created = convertingTime.toLocaleString();
            }
        },

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