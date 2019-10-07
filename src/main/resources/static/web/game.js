new Vue({
    el: "#game",
    data: {
        game: [],
        tableRows: ['', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
        tableCols: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'],
        gamePlayerId: 0,
        gamePlayerYou: 0,
        gamePlayerOther: 0,
        shipLocations: [],
        mySalvoesObject: 0,
        mySalvoes:[],
        enemySalvoesObject: 0,
        enemySalvoes:[],
        damagedShipLocations: [],

    },
    methods: {


        fetchData: function () {
            fetch("http://localhost:8080/api/game_view/1")
                .then(function (response) {
                    console.log(response);
                    return response.json();
                })
                .then((data) => {
                    console.log(data);
                    this.game = data;
                    this.isLoaded();
                })
                .catch(function (error) {
                    console.log(error);
                })
        },

        isLoaded: function () {
            if (this.game.length == 0) {
                return false;
            } else {
                this.getParamsFromUrl();
                this.makeGPShipsArray();
                return true
            }
        },

        getParamsFromUrl() {
            var parsedUrl = new URL(window.location.href);
            this.gamePlayerId = (parsedUrl.searchParams.get("gp"));
            if (this.game[0].gamePlayers[1].id == this.gamePlayerId) {
                this.gamePlayerYou = this.game[0].gamePlayers[1].player.email
                this.gamePlayerOther = this.game[0].gamePlayers[0].player.email
                this.mySalvoesObject = this.game[0].gamePlayers[1].salvoes
                this.enemySalvoesObject= this.game[0].gamePlayers[0].salvoes
            } else {
                this.gamePlayerYou = this.game[0].gamePlayers[0].player.email
                this.gamePlayerOther = this.game[0].gamePlayers[1].player.email
                this.mySalvoesObject = this.game[0].gamePlayers[0].salvoes
                this.enemySalvoesObject= this.game[0].gamePlayers[1].salvoes
            }
            this.fillArrMySalvoes()
            this.fillArrEnemySalvoes()
            console.log(this.mySalvoesObject)
        },

        makeGPShipsArray() {
            for (j in this.game[0].ships) {
                for (i in this.game[0].ships[j].locations) {
                    this.shipLocations.push(this.game[0].ships[j].locations[i])
                }
            }
            this.fillArrDamagedShipLocations();
            this.displayMySalvoes();
        },

        displayShips() {
                for (i in this.shipLocations) {
                    document.getElementById(this.shipLocations[i]).style.backgroundColor = "grey";
                }
                for (j in this.damagedShipLocations){
                    document.getElementById(this.damagedShipLocations[j]).innerHTML='<i class="glyphicon glyphicon-remove" style="font-size:16px;color:purple;"></i>';   
                }
        },

        fillArrDamagedShipLocations(){
           this.shipLocations.forEach(oneShipLocation=>{
               this.enemySalvoes.map(oneEnemySalvo=>{
                   if(oneEnemySalvo == oneShipLocation){
                    this.damagedShipLocations.push(oneShipLocation)
                   }
               })
           })
           this.displayShips();
        },

        fillArrMySalvoes(mySalvoes, mySalvoesObject){
            for (i in this.mySalvoesObject){
                this.mySalvoes.push.apply(this.mySalvoes,this.mySalvoesObject[i]);
            }
        },

        fillArrEnemySalvoes(enemySalvoes, enemySalvoesObject){
            for (i in this.enemySalvoesObject){
                this.enemySalvoes.push.apply(this.enemySalvoes,this.enemySalvoesObject[i]);
            }
        },
        displayMySalvoes(){
            for (i in this.mySalvoes){
                document.getElementById(this.shipLocations[i]+"s").innerHTML='<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:purple;background-color:grey;"></i>'; 
                document.getElementById(this.shipLocations[i]+"s").style.backgroundColor = "grey"; 
            }
        }
    },

    created: function () {
        this.fetchData();
    },
})

/* 
function paramObj(search) {
  var obj = {};
  var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

  search.replace(reg, function(match, param, val) {
    obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
  });

  return obj;
}
*/