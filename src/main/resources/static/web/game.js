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
        hitsAndSinksObjectYou: {},
        hitsAndSinksObjectOpponent: {},
        hitsOther: [],
        ships: [],
        message: null,
        objectForClearingShips: [],
        gameOver: false,
        myScore: 1,
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
                this.gameOver = this.game[0].over;
                console.log(this.game[0].turn)
                if(this.game[0].turn==true){
                    this.message="Place salvoes"
                   /*  setTimeout(() => {
                        this.message = null;
                    }, 3000); */

                }
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
                this.hitsAndSinksObjectYou = this.game[0].gamePlayers[1].status
                this.hitsAndSinksObjectOpponent = this.game[0].gamePlayers[0].status
                this.hitsOther = this.game[0].gamePlayers[0].hits
            } else {
                this.gamePlayerYou = this.game[0].gamePlayers[0].player.email
                this.gamePlayerOther = this.game[0].gamePlayers[1].player.email
                this.mySalvoesObject = this.game[0].gamePlayers[0].salvoes
                this.enemySalvoesObject = this.game[0].gamePlayers[1].salvoes
                this.hitsAndSinksObjectYou = this.game[0].gamePlayers[0].status
                this.hitsAndSinksObjectOpponent = this.game[0].gamePlayers[1].status
                this.hitsOther = this.game[0].gamePlayers[1].hits
            }
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
            console.log(this.numberPointF, this.shipLength)
            var i = this.numberPointF - this.shipLength; // is my ship within the grid?
            temporaryArray1 = this.tableRows.slice(); //horizontal axis
            temporaryArray2 = this.tableCols.slice(); //vertical axis
            manipulableShipLength = this.shipLength
            temporaryArray2ValidityCheck = false; //to make sure the length of the ship is within the grid

            if (temporaryArray2.length - temporaryArray2.indexOf(this.letterPointF + "") >= this.shipLength1) {
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
            //if the horisontal coordinates are on the grid && vertical coordinates are on the grid
            if (i >= 0 && temporaryArray2ValidityCheck == true) {
                temporaryArray2.splice(0, temporaryArray2.indexOf(this.letterPointF + "") + 1);
                this.shipLength -= 1
            } else if (temporaryArray2ValidityCheck == true) { //if else vertical coordinates are on the grid
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
            //intersection: vertical axis
            var q;
            for (q in this.objectForDisplayingSelectedShipsNoManipulation) {
                this.shipLocationsAsOneArray.push.apply(this.shipLocationsAsOneArray, this.objectForDisplayingSelectedShipsNoManipulation[q].locations[0])
            }
            var overlapCoordinatesVerticalAxes = []
            for (l in this.verticalAxis) {
                var b;
                b = this.shipLocationsAsOneArray.filter(oneCoordinate => oneCoordinate == this.verticalAxis[l])
                if (b.length != 0) {
                    overlapCoordinatesVerticalAxes.push(b)
                }
            }
            if (overlapCoordinatesVerticalAxes.length > 0) {
                this.verticalIntersection = true;
            } else {
                this.verticalIntersection = false;
            }
            //intersection: horisontal axis
            var overlapCoordinatesHorisontalAxes = []
            for (m in this.horisontalAxis) {
                var b;
                b = this.shipLocationsAsOneArray.filter(oneCoordinate => oneCoordinate == this.horisontalAxis[m]);
                if (b.length > 0) {
                    overlapCoordinatesHorisontalAxes.push(b)
                }
            }
            if (overlapCoordinatesHorisontalAxes.length > 0) {
                this.horizontalIntersection = true;
            } else {
                this.horizontalIntersection = false;
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
                    if (this.lastCoordinate != 0) { //=GP clicked on the end of the ship
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
                this.objectForClearingShips.push(this.objectForDisplayingSelectedShips[0]);
                console.log(this.objectForClearingShips)
                this.objectForDisplayingSelectedShips = [];
            }
        },

        clearShips() {
            for (i in this.objectForClearingShips) {
                var shipLocation = this.objectForClearingShips[i].locations[0]
                for (j in shipLocation) {
                    document.getElementById(shipLocation[j] + "ps").style.backgroundColor = "thistle";
                    var img = document.createElement("img");
                    img.id = (shipLocation[j] + "psShipPic")
                    img.src = "assets/" + this.objectForClearingShips[i].type + ".pur.png";
                    var src = document.getElementById(shipLocation[j] + "ps");
                    var shipToBeRemoved = document.getElementById(img.id);
                    shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                    src.style.backgroundColor = "#40E0D0"

                    //make ships visible in the option table
                    document.getElementById(this.objectForClearingShips[i].type).style.display = "inline"

                }
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
            this.objectForDisplayingSelectedShipsNoManipulation = [];
            this.showConfirmButton = false;
            this.showClearButton = false;
            this.objectForClearingShips = [];
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
                    this.message = response;
                    /* setTimeout(() => {
                        this.message = null;
                    }, 5000); */
                    // alert(response)
                    this.fetchData();
                    this.removeShipOptionsTable = true;
                    this.makeCallsEveryFiveSecs();
                })
                .fail(function (jqXHR, status, httpError) {
                    alert("Failed to add ships: " + textStatus + " " + httpError);
                })
        },

        //after ships are placed
        makeGPShipsArray() {
            if (this.game[0].ships.length == 0) {
                this.shipsPlaced = false;
                this.message = "Welcome " + this.gamePlayerYou + "!"
                setTimeout(() => {
                    this.message = "Place your ships";
                }, 3000);
                setTimeout(() => {
                    this.message = null;
                }, 8000);
            }
            if (this.game[0].ships.length != 0 || this.game[0].ships.length != 0 && this.mySalvoes.length > 0) {
                this.ships = this.game[0].ships
                this.shipsPlaced = true;
                this.removeShipOptionsTable = true;
                for (j in this.game[0].ships) {
                    for (i in this.game[0].ships[j].locations) {
                        this.shipLocations.push(this.game[0].ships[j].locations[i])
                    }
                }
                this.fillArrDamagedShipLocations();
            }

        },

        slowingTheOnsetOfFunctions() {
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
            this.displayMySalvoes();
            setTimeout(() => {
                this.displayShips();
            }, 100);
        },

        displayShips() {
            for (i in this.ships) {
                for (j in this.ships[i].locations) {
                    if (document.getElementById(this.ships[i].locations[j]).innerHTML === '') {
                        console.log("emptyDoc")
                        document.getElementById(this.ships[i].locations[j]).style.backgroundColor = "thistle";
                        var img = document.createElement("img");
                        img.src = "assets/" + this.ships[i].type + ".pur.png";
                        var src = document.getElementById(this.ships[i].locations[j])
                        src.appendChild(img);
                    } else {
                        console.log("!emptyDoc")
                        document.getElementById(this.ships[i].locations[j]).style.backgroundColor = "thistle";
                        var img = document.createElement("img");
                        img.src = "assets/" + this.ships[i].type + ".pur.png";
                        var src = document.getElementById(this.ships[i].locations[j]);
                        src.appendChild(img);
                        img.id = (this.ships[i].locations[j] + "psShipPic")
                        var shipToBeRemoved = document.getElementById(img.id);
                        shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                    }

                }
            }

            for (j in this.damagedShipLocations) {
                document.getElementById(this.damagedShipLocations[j]).innerHTML = '<i class="glyphicon glyphicon-fire" style="font-size:16px;color:mediumorchid;"></i>';
            }
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
                    document.getElementById(coordinateElements + "s").innerHTML = "";
                    document.getElementById(coordinateElements + "s").style.backgroundColor = "#40E0D0";
                } else {
                    this.salvoesInThisTurn.push(coordinateElements)
                }
                this.displayFiredSalvoesInThisTurn()
            } else {
                this.message = "You've fired all salvoes"
            }
            if (this.salvoesInThisTurn.length == 5) {
                this.showConfirmSalvoesButton = true;
            }
        },

        displayFiredSalvoesInThisTurn() {
            for (i in this.salvoesInThisTurn) {
                document.getElementById(this.salvoesInThisTurn[i] + "s").innerHTML = '<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:mediumorchid;background-color:thistle;"></i>';
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
                    this.message = response;

                    this.shipsPlaced = true;
                    this.fetchData();
                    setTimeout(()=>{
                        this.displayingGameResults(),4000
                    })
                   setTimeout(() => {
                        this.message = null;
                    }, 5000);
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

            for (j in this.hitsOther) {
                document.getElementById(this.hitsOther[j] + "s").innerHTML = '<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:mediumorchid;background-color:thistle;"></i>';
            }
        },
        makeCallsEveryFiveSecs() {
            setInterval(() => {
                this.fetchData()
            }, 10000);
        },

       /*  displayingGameResults(){
            setTimeout(() => {
                this.displayingGameResults2() ;
            }, 8000);
        }, */
    
        displayingGameResults() {
            if (this.gameOver == true) {
                if (this.myScore == 1) {
                    this.message=null;
                    document.getElementById("ship-you").className = "moving-ship-you-win"
                    document.getElementById("ship-enemy").className = "moving-ship-enemy-lose"
                    /* document.getElementById("opponent-shot1").style.display = "none"
                    document.getElementById("shot-you1").style.display = "none"
                    document.getElementById("firework-left1").style.display = "none"
                    document.getElementById("firework-right1").style.display = "none" */
                    setTimeout(() => document.getElementById("opponent-shot1").className = "opponent-shot", 2000);
                    setTimeout(() => document.getElementById("shot-you1").className = "your-shot", 2000);
                    setTimeout(() => document.getElementById("opponent-shot1").style.display = "inline", 2000);
                    setTimeout(() => document.getElementById("shot-you1").style.display = "inline", 2000);
                    setTimeout(() => document.getElementById("opponent-shot1").style.display = "none", 2600);
                    setTimeout(() => document.getElementById("shot-you1").style.display = "none", 2600);
                    //first firework 
                    setTimeout(() => document.getElementById("firework-left1").className = "firework-left", 4500);
                    setTimeout(() => document.getElementById("firework-left1").style.display = "inline", 4500);
                    setTimeout(() => document.getElementById("firework-left1").style.display = "none", 5000);
                    //second firework
                    setTimeout(() => document.getElementById("firework-right1").className = "firework-right", 5500);
                    setTimeout(() => document.getElementById("firework-right1").style.display = "inline", 5500);
                    setTimeout(() => document.getElementById("firework-right1").style.display = "none", 6000);
                    //third firework
                    setTimeout(() => document.getElementById("firework-left1").className = "firework-left", 6500);
                    setTimeout(() => document.getElementById("firework-left1").style.display = "inline", 6500);
                    setTimeout(() => document.getElementById("firework-left1").style.display = "none", 7000);
                    //second firework
                    setTimeout(() => document.getElementById("firework-right1").className = "firework-right", 7500);
                    setTimeout(() => document.getElementById("firework-right1").style.display = "inline", 7500);
                    setTimeout(() => document.getElementById("firework-right1").style.display = "none", 7900);


                    // setTimeout(() => document.getElementById(this.selectedShipType).style.display = "none", 0);
                    // document.getElementById("opponent-shot1").className = "opponent-shot"
                    // document.getElementById("shot-you1").className = "your-shot"
                    // document.getElementById("firework-left1").className = "firework-left"
                    //  document.getElementById("firework-right1").className = "firework-right"

                    // document.getElementById("opponent-shot1").style.display = "none"
                    // document.getElementById("shot-you1").style.display = "none"
                    document.getElementById("firework-left1").style.display = "none"
                    document.getElementById("firework-right1").style.display = "none"


                } else if (this.myScore == 0) {
                    document.getElementById("ship-you").className = "moving-ship-you-lose"
                    document.getElementById("ship-enemy").className = "moving-ship-enemy-win"
                } else {
                    // document.getElementById("ship-you").className = ""
                    //document.getElementById("ship - enemy").className = ""
                }
            } else {
                document.getElementById("opponent-shot1").style.display = "none";
                document.getElementById("shot-you1").style.display = "none";
                document.getElementById("firework-left1").style.display = "none";
                document.getElementById("firework-right1").style.display = "none";
            }
        }
    },

    created: function () {
        this.fetchData();
        this.slowingTheOnsetOfFunctions();
    },
})