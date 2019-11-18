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
        mySalvoes: [],
        enemySalvoesObject: 0,
        enemySalvoes: [],
        damagedShipLocations: [],
        shipsPlaced: false,
        selectedShipType: 0,
        //manipulated:
        shipLength: 0,
        //non-manipulated:
        shipLength1: 0,
        carrier: "carrier",
        battleship: "battleship",
        submarine: "submarine",
        destroyer: "destroyer",
        patrol: "patrol",
        removeShipOptionsTable: false,
        firstCoordinate: 0,
        lastCoordinate: 0,
        letterPointF: 0,
        numberPointF: 0,
        letterPointL: 0,
        numberPointL: 0,
        objectForDisplayingSelectedShips: [],
        objectForDisplayingSelectedShipsNoManipulation: [],
        shipLocationsAsOneArray: [],
        horisontalAxis: [],
        verticalAxis: [],
        showConfirmButton: false,
        showClearButton: false,
        counter: 0,
        verticalIntersection: false,
        horizontalIntersection: false,
        salvoesInThisTurn: [],
        turns: 0,
        showConfirmSalvoesButton: false,
        images: ["assets/carrier.pur.png", "assets/battleship.pur.png", "assets/submarine.pur.png", "assets/destroyer.pur.png", "assets/patrol.pur.png"],
        hitsAndSinksObjectYou:{},
        hitsAndSinksObjectOpponent:{}, 
        hitsOther:[], 
        ships:[],  
    },
    methods: {
        fetchData: function () {
            var parsedUrl = new URL(window.location.href);
            var gpId = (parsedUrl.searchParams.get("gp"));
            fetch("http://localhost:8080/api/game_view/" + gpId)
                .then(function (response) {
                    console.log(response);
                    return response.json();
                })
                .then((data) => {
                    console.log(data);
                    console.log("hello")
                    //console.log(this.hitsAndSinksObject[0])
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
                this.enemySalvoesObject = this.game[0].gamePlayers[0].salvoes
                this.hitsAndSinksObjectYou=this.game[0].gamePlayers[1].status
                this.hitsAndSinksObjectOpponent=this.game[0].gamePlayers[0].status
                this.hitsOther=this.game[0].gamePlayers[0].hits
            } else {
                this.gamePlayerYou = this.game[0].gamePlayers[0].player.email
                this.gamePlayerOther = this.game[0].gamePlayers[1].player.email
                this.mySalvoesObject = this.game[0].gamePlayers[0].salvoes
                this.enemySalvoesObject = this.game[0].gamePlayers[1].salvoes
                this.hitsAndSinksObjectYou=this.game[0].gamePlayers[0].status
                this.hitsAndSinksObjectOpponent=this.game[0].gamePlayers[1].status
                this.hitsOther=this.game[0].gamePlayers[1].hits
            }
            console.log(this.hitsAndSinksObjectYou)
            console.log(this.hitsOther)
            this.displayWhereYouHitYourOpponent();
            this.fillArrMySalvoes()
            this.fillArrEnemySalvoes()
        },

        //before ships are placed
        //this recieves info about what ship's been selected from the ship table
        getShipTypeFromPlacementGrid(shipTypeFromGrid, lenght) {
            this.shipLength = lenght
            this.shipLength1 = this.shipLength
            this.selectedShipType = shipTypeFromGrid;
            setTimeout(() => document.getElementById(this.selectedShipType).style.display = "none", 0);
        },
        //this analyses coordinates from html grid, where the player wants to place a ship. 
        placeShipInThisCell(elementId) {
            var mystring;
            var str
            var coordinateElements
            mystring = elementId;
            mystring = mystring.replace(/ps/g, '');
            str = mystring;
            coordinateElements = str.split(/(\d+)/);
            console.log(coordinateElements)
            if (this.letterPointF == 0) {
                this.letterPointF = coordinateElements[0];
                this.numberPointF = coordinateElements[1];
            } else if (this.letterPointF == coordinateElements[0] || this.numberPointF == coordinateElements[1]) {
                //this condition aims at preventing invalid ship placement
                this.letterPointL = coordinateElements[0];
                this.numberPointL = coordinateElements[1];
            } else {
                alert("invalid ship placement")
                this.letterPointL = 0,
                    this.numberPointL = 0
            }

            if (this.firstCoordinate == 0) {
                this.firstCoordinate = mystring;
                this.displayShipPlacementOptions1()
            } else {
                this.lastCoordinate = mystring
                this.displayShipPlacementOptions2()
            }

        },
        //display ship placement options and fill up the this.objectForDisplayingSelectedShips
        displayShipPlacementOptions1() {
            var temporaryArray1 = [];
            var temporaryArray2 = [];
            var i = this.numberPointF - this.shipLength; // is my ship within the grid?
            temporaryArray1 = this.tableRows.slice(); //horizontal axis
            temporaryArray2 = this.tableCols.slice(); //vertical axis
            manipulableShipLength = this.shipLength
            temporaryArray2ValidityCheck = false;

            if (temporaryArray2.length - temporaryArray2.indexOf(this.letterPointF + "") > this.shipLength1) {
                temporaryArray2ValidityCheck = true;
            } else {
                temporaryArray2ValidityCheck = false;
            }



            //horizontal axis
            temporaryArray1.length = this.shipLength;
            if (i >= 0) {
                for (j in temporaryArray1) {
                    this.horisontalAxis.push(this.letterPointF + this.numberPointF--)
                }
                this.numberPointF += this.shipLength
            }
            //vertical axis
            if (i >= 0 && temporaryArray2ValidityCheck == true) {
                temporaryArray2.splice(0, temporaryArray2.indexOf(this.letterPointF + "") + 1);
                this.shipLength -= 1
            } else if (temporaryArray2ValidityCheck == true) {
                temporaryArray2.splice(0, temporaryArray2.indexOf(this.letterPointF + ""));
            } else(temporaryArray2 = [])
            //vertical axis
            temporaryArray2.splice(this.shipLength, temporaryArray2.length);
            if (temporaryArray2.length == this.shipLength) {
                var k;
                for (k in temporaryArray2) {
                    this.verticalAxis.push(temporaryArray2[k] + this.numberPointF)
                }
            }
            //checking if there are intersections in placed ships and displayed options 

            for (q in this.objectForDisplayingSelectedShipsNoManipulation) {
                this.shipLocationsAsOneArray.push(this.objectForDisplayingSelectedShipsNoManipulation[q].locations[0])
            }
            console.log(this.shipLocationsAsOneArray, this.selectedShipType)

            for (l in this.verticalAxis) {
                if (this.shipLocationsAsOneArray.includes(this.verticalAxis[l])) {
                    console.log(this.shipLocationsAsOneArray)
                    this.verticalIntersection = true;
                } else {
                    this.verticalIntersection = false;
                }
            }
            for (m in this.horisontalAxis) {
                if (this.shipLocationsAsOneArray.includes(this.horisontalAxis[m])) {
                    this.horizontalIntersection = true;
                } else {
                    this.horizontalIntersection = false;
                }
            }

            this.displayShipPlacementOptions2()
        },
        displayShipPlacementOptions2() {

            if (this.verticalIntersection == false) {
                for (l in this.verticalAxis) {
                    if (this.lastCoordinate != 0) {
                        var img = document.createElement("img");
                        img.id = this.selectedShipType + l + 'vA'
                        img.src = "assets/" + this.selectedShipType + ".pur.png";
                        var src = document.getElementById(this.verticalAxis[l] + "ps");
                        src.classList.remove("shake")
                        var shipToBeRemoved = document.getElementById(img.id);
                        shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                        src.style.backgroundColor = "#40E0D0"
                    } else {
                        document.getElementById(this.verticalAxis[l] + "ps").style.backgroundColor = "thistle";
                        var img = document.createElement("img");
                        img.id = this.selectedShipType + l + 'vA'
                        img.src = "assets/" + this.selectedShipType + ".pur.png";
                        var src = document.getElementById(this.verticalAxis[l] + "ps");
                        src.appendChild(img);
                        src.classList.add("shake")
                    }
                }
            }

            if (this.horizontalIntersection == false) {
                for (m in this.horisontalAxis) {
                    if (this.lastCoordinate != 0) {
                        /*document.getElementById(this.horisontalAxis[m] + "ps").innerHTML=""
                        document.getElementById(this.horisontalAxis[m] + "ps").style.backgroundColor = "#40E0D0"*/
                        var img = document.createElement("img");
                        img.id = this.selectedShipType + m + 'hA'
                        img.src = "assets/" + this.selectedShipType + ".pur.png";
                        var src = document.getElementById(this.horisontalAxis[m] + "ps");
                        src.classList.remove("shake")
                        var shipToBeRemoved = document.getElementById(img.id);
                        shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                        src.style.backgroundColor = "#40E0D0"

                    } else {
                        document.getElementById(this.horisontalAxis[m] + "ps").style.backgroundColor = "thistle";
                        var img = document.createElement("img");
                        img.id = this.selectedShipType + m + 'hA'
                        img.src = "assets/" + this.selectedShipType + ".pur.png";
                        var src = document.getElementById(this.horisontalAxis[m] + "ps");
                        src.appendChild(img);
                        src.classList.add("shake")
                    }
                }
            }

            var shipLocationArray = []
            var verticalAxisReplacementArray = this.verticalAxis.slice();
            if (verticalAxisReplacementArray.length != this.shipLength1 && verticalAxisReplacementArray.length != 0) {
                var coordinateString = []
                coordinateString = verticalAxisReplacementArray[0].split(/(\d+)/);
                coordinateString = this.tableCols[this.tableCols.indexOf(coordinateString[0]) - 1] + coordinateString[1]
                verticalAxisReplacementArray.push(coordinateString)
            }
            if (this.horisontalAxis.includes(this.lastCoordinate)) {
                shipLocationArray.push(this.horisontalAxis)
            } else {
                shipLocationArray.push(verticalAxisReplacementArray)
            }


            if (this.lastCoordinate != 0) {
                this.objectForDisplayingSelectedShips.push({
                    "type": "" + this.selectedShipType,
                    "locations": shipLocationArray
                })
                this.objectForDisplayingSelectedShipsNoManipulation.push(this.objectForDisplayingSelectedShips[0])
                this.counter++;
                if (this.counter > 4) {
                    this.showConfirmButton = true;
                } else {
                    this.showConfirmButton = false;
                }

                if (this.counter >= 1) {
                    this.showClearButton = true;
                } else {
                    this.showClearButton = false;
                }

                this.selectedShipType = 0,
                    this.shipLength = 0,
                    this.firstCoordinate = 0,
                    this.lastCoordinate = 0,
                    this.letterPointF = 0,
                    this.numberPointF = 0,
                    this.letterPointL = 0,
                    this.numberPointL = 0,
                    this.horisontalAxis = [];
                this.verticalAxis = [];
                shipLocationArray = [];
                this.displayShipsBeforePlacement()
            }
            console.log(this.objectForDisplayingSelectedShipsNoManipulation)
        },

        displayShipsBeforePlacement() {
            for (i in this.objectForDisplayingSelectedShips) {
                var shipLocation = this.objectForDisplayingSelectedShips[i].locations[0]
                for (j in shipLocation) {
                    document.getElementById(shipLocation[j] + "ps").style.backgroundColor = "thistle";
                    var img = document.createElement("img");
                    img.id = (shipLocation[j] + "psShipPic")
                    img.src = "assets/" + this.objectForDisplayingSelectedShips[i].type + ".pur.png";
                    var src = document.getElementById(shipLocation[j] + "ps");
                    src.appendChild(img);
                }
                this.objectForDisplayingSelectedShips = [];
            }
        },

        sendShips() {
            var objectTypeLocation = []
            for (i in this.objectForDisplayingSelectedShipsNoManipulation) {
                objectTypeLocation.push({
                    type: this.objectForDisplayingSelectedShipsNoManipulation[i].type,
                    locations: this.objectForDisplayingSelectedShipsNoManipulation[i].locations[0]
                })
                //document.getElementById(this.objectForDisplayingSelectedShipsNoManipulation[i]).innerHTML = "";
                //document.getElementById(this.objectForDisplayingSelectedShipsNoManipulation[i]).style.backgroundColor = "#40E0D0"
            }
            console.log(objectTypeLocation)
            $.post({
                    url: "/api/games/player/" + this.gamePlayerId + "/ships",
                    data: JSON.stringify(objectTypeLocation),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done((response, status, jqXHR) => {
                    alert(response);
                    // this.fetchData();
                    this.removeShipOptionsTable = true;

                })
                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to add ships: " + textStatus + " " + httpError);
                })
            console.log("shipsSent")
        },

        //after ships are placed
        makeGPShipsArray() {
            if (this.game[0].ships.length == 0) {
                this.shipsPlaced = false;
            }
            if (this.game[0].ships.length != 0 || this.game[0].ships.length != 0 && this.mySalvoes.length > 0) {
                this.ships=this.game[0].ships
                this.shipsPlaced = true;
                this.removeShipOptionsTable = true;
                for (j in this.game[0].ships) {
                    for (i in this.game[0].ships[j].locations) {
                        this.shipLocations.push(this.game[0].ships[j].locations[i])
                    }
                }
                // this.clearDisplayedShips()
                this.fillArrDamagedShipLocations();
            }

        },

        slowingTheOnsetOfFunctions(){
            window.addEventListener('load', (event) => {
                this.displayMySalvoes();
                this.displayShips();
            console.log('page is fully loaded');
          });
           },

        fillArrDamagedShipLocations() {
            this.shipLocations.forEach(oneShipLocation => {
                this.enemySalvoes.map(oneEnemySalvo => {
                    if (oneEnemySalvo == oneShipLocation) {
                        this.damagedShipLocations.push(oneShipLocation)
                    }
                })
            })
            console.log("displayShips is called")
            //setTimeout( () => this.clearDisplayedShips(), 50)
            // setTimeout( () => this.displayShips(), 100)
            this.displayShips()
            this.displayMySalvoes();
        },

        /*  clearDisplayedShips(){
             console.log(this.shipLocations)
             for (i in this.shipLocations) {
                 document.getElementById(this.shipLocations[i]).innerHTML = "";
                 document.getElementById(this.shipLocations[i]).style.backgroundColor = "#40E0D0"
             }  
         },  */

        displayShips() {
           
            //function for displaying different icons of ships
            /* console.log(this.shipLocations)
            var shipDisplayArrayTypeLocation = []
            for (l in this.game[0].ships) {
                shipDisplayArrayTypeLocation.push(this.game[0].ships[l].type, this.game[0].ships[l].locations)
                console.log(shipDisplayArrayTypeLocation)
            }
            var shipLocationArrayFromTypeLocationsArray = []
            var shipTypeFromTypeLocationsArray
            for (m in shipDisplayArrayTypeLocation) {

                if (m % 2 == 1) {
                    shipLocationArrayFromTypeLocationsArray = shipDisplayArrayTypeLocation[m]
                }
                if (m % 2 == 0) {
                    shipTypeFromTypeLocationsArray = shipDisplayArrayTypeLocation[m]}
                    for (g in shipLocationArrayFromTypeLocationsArray) {
                        document.getElementById(shipLocationArrayFromTypeLocationsArray[g]).style.backgroundColor = "thistle";
                        var img = document.createElement("img");
                        img.id = shipTypeFromTypeLocationsArray + shipLocationArrayFromTypeLocationsArray[g]
                        img.src = "assets/" + shipTypeFromTypeLocationsArray + ".pur.png";
                        var src = document.getElementById(shipLocationArrayFromTypeLocationsArray[g]);
                        src.appendChild(img);
                        console.log(shipLocationArrayFromTypeLocationsArray[g], shipTypeFromTypeLocationsArray)
                    }
                
            } 
            
             for (i in this.shipLocations) {
                document.getElementById(this.shipLocations[i]).style.backgroundColor = "thistle";
                var img = document.createElement("img");
                img.src = "assets/battleship.pur.png";
                var src = document.getElementById(this.shipLocations[i]);
                src.appendChild(img);
                img.id = (this.shipLocations[i] + "psShipPic")
                var shipToBeRemoved = document.getElementById(img.id);
                shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)

            }
            */
            for (i in this.ships) {
                console.log(this.ships[i].type)
                for (j in this.ships[i].locations){
                    document.getElementById(this.ships[i].locations[j]).style.backgroundColor = "thistle";
                    var img = document.createElement("img");
                    img.src = "assets/"+this.ships[i].type+".pur.png";
                    var src = document.getElementById(this.ships[i].locations[j]);
                    src.appendChild(img);
                    img.id = (this.ships[i].locations[j]+ "psShipPic")
                    var shipToBeRemoved = document.getElementById(img.id);
                    shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                  
                }
                

            }

            for (j in this.damagedShipLocations) {
                document.getElementById(this.damagedShipLocations[j]).innerHTML = '<i class="glyphicon glyphicon-remove" style="font-size:16px;color:purple;"></i>';
            }
            console.log(this.shipLocations)
        },

        placeSalvoInThisCell(elementId) {
            var mystring;
            var coordinateElements
            mystring = elementId;
            mystring = mystring.replace(/s/g, '');
            coordinateElements = mystring;
            if (this.salvoesInThisTurn.length < 5) {
                if (this.salvoesInThisTurn.includes(coordinateElements)) {
                    this.salvoesInThisTurn = this.salvoesInThisTurn.filter(oneCoordinate => oneCoordinate != coordinateElements)
                    console.log(this.salvoesInThisTurn)
                    document.getElementById(coordinateElements + "s").innerHTML = "";
                    document.getElementById(coordinateElements + "s").style.backgroundColor = "#40E0D0";
                } else {
                    this.salvoesInThisTurn.push(coordinateElements)
                    console.log(this.salvoesInThisTurn)
                }
                this.displayFiredSalvoesInThisTurn()
            } else {
                alert("You've fired all salvoes")
            }
            if (this.salvoesInThisTurn.length == 5) {
                this.showConfirmSalvoesButton = true;
            }
        },

        displayFiredSalvoesInThisTurn() {
            for (i in this.salvoesInThisTurn) {
                document.getElementById(this.salvoesInThisTurn[i] + "s").innerHTML = '<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:purple;background-color:thistle;"></i>';
                document.getElementById(this.salvoesInThisTurn[i] + "s").style.backgroundColor = "thistle";
            }
        },

        sendSalvoes() {
            for (i in this.salvoesInThisTurn) {
                document.getElementById(this.salvoesInThisTurn[i] + "s").innerHTML = "";
                document.getElementById(this.salvoesInThisTurn[i] + "s").style.backgroundColor = "#40E0D0";
            }
            var turn = 1;
            $.post({
                    url: "/api/games/player/" + this.gamePlayerId + "/salvoes",
                    data: JSON.stringify({
                        locations: this.salvoesInThisTurn
                    }),
                    dataType: "text",
                    contentType: "application/json"
                })
                .done((response, status, jqXHR) => {
                    alert(response);
                    this.shipsPlaced = true;
                    this.fetchData();
                })
                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to add ships: " + textStatus + " " + httpError);
                })
            this.showConfirmSalvoesButton = false;
            this.salvoesInThisTurn = []

        },

        clearSalvoes() {
            for (i in this.salvoesInThisTurn) {
                document.getElementById(this.salvoesInThisTurn[i] + "s").innerHTML = "";
                document.getElementById(this.salvoesInThisTurn[i] + "s").style.backgroundColor = "#40E0D0";
            }
            this.salvoesInThisTurn = []
        },
        fillArrMySalvoes(mySalvoes, mySalvoesObject) {
            for (i in this.mySalvoesObject) {
                this.mySalvoes.push.apply(this.mySalvoes, this.mySalvoesObject[i]);
            }
            console.log(this.mySalvoes)
        },

        fillArrEnemySalvoes(enemySalvoes, enemySalvoesObject) {
            for (i in this.enemySalvoesObject) {
                this.enemySalvoes.push.apply(this.enemySalvoes, this.enemySalvoesObject[i]);
            }
        },
        displayMySalvoes() {
            for (i in this.mySalvoes) {
                document.getElementById(this.mySalvoes[i] + "s").style.backgroundColor = "thistle";
            }

            for (j in this.hitsOther){
                document.getElementById(this.hitsOther[j] + "s").innerHTML = '<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:purple;background-color:thistle;"></i>';
            }
        },

        displayWhereYouHitYourOpponent(){
            console.log(this.mySalvoes)
            console.log(this.hitsOther)
            var arrayWhereYourOpponentIsHit=[]
         
        },
        
        /*makeCallsEveryFiveSecs(){
            setInterval(()=> { this.fetchData() }, 10000);}*/
    },

    created: function () {
        this.fetchData();
        this.slowingTheOnsetOfFunctions();
       // this.makeCallsEveryFiveSecs();
    },
})