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
        shipsPlaced: true,
        selectedShipType: 0,
        shipLength: 0,
        shipLength1: 0,
        carrier: "carrier",
        battleship: "battleship",
        submarine: "submarine",
        destroyer: "destroyer",
        patrol: "patrol",
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

    },
    methods: {
        fetchData: function () {
            var parsedUrl = new URL(window.location.href);
            var gpId = (parsedUrl.searchParams.get("gp"));
            console.log(gpId);
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
            } else {
                this.gamePlayerYou = this.game[0].gamePlayers[0].player.email
                this.gamePlayerOther = this.game[0].gamePlayers[1].player.email
                this.mySalvoesObject = this.game[0].gamePlayers[0].salvoes
                this.enemySalvoesObject = this.game[0].gamePlayers[1].salvoes
            }
            this.fillArrMySalvoes()
            this.fillArrEnemySalvoes()
            console.log(this.mySalvoesObject)
        },

        //before ships are placed
        //this recieves info about what ship's been selected
        getShipTypeFromPlacementGrid(shipTypeFromGrid, lenght) {
            this.shipLength = lenght
            this.shipLength1 = this.shipLength
            this.selectedShipType = shipTypeFromGrid;
            console.log(this.selectedShipType, this.shipLength)
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
            coordinateElements = str.split('');
            console.log(coordinateElements)
            if (this.letterPointF == 0) {
                this.letterPointF = coordinateElements[0];
                this.numberPointF = coordinateElements[1];
            } else if (this.letterPointF == coordinateElements[0] || this.numberPointF == coordinateElements[1]) {
                this.letterPointL = coordinateElements[0];
                this.numberPointL = coordinateElements[1];
            } else {
                alert("no diagonal ship placement")
                this.letterPointL = 0,
                    this.numberPointL = 0
            }
            console.log(this.letterPointF, this.numberPointF, this.letterPointL, this.numberPointL)
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
            var manipulableShipLength;
            console.log(this.numberPointF)
            var i = this.numberPointF - this.shipLength; // is my ship within the grid?
            temporaryArray1 = this.tableRows.slice(); //horizontal axis
            temporaryArray2 = this.tableCols.slice(); //vertical axis
            manipulableShipLength = this.shipLength
            //horizontal axis
            temporaryArray1.length = this.shipLength;
            console.log(i)
            if (i >= 0) {
                for (j in temporaryArray1) {
                    console.log(this.numberPointF)
                    this.horisontalAxis.push(this.letterPointF + this.numberPointF--)
                }
                console.log(this.horisontalAxis)
                this.numberPointF += this.shipLength;
                //vertical axis
                temporaryArray2.splice(0, temporaryArray2.indexOf(this.letterPointF + "") + 1);
                this.shipLength -= 1
                console.log(this.shipLength)
                console.log(manipulableShipLength)
            } else {
                temporaryArray2.splice(0, temporaryArray2.indexOf(this.letterPointF + ""));
            }
            //vertical axis
            temporaryArray2.splice(this.shipLength, temporaryArray2.length);
            console.log(temporaryArray2)
            if (temporaryArray2.length == this.shipLength) {
                var k;
                for (k in temporaryArray2) {
                    console.log(temporaryArray2[k], this.numberPointF)
                    this.verticalAxis.push(temporaryArray2[k] + this.numberPointF)
                }
                console.log(this.verticalAxis)
            }
            this.displayShipPlacementOptions2()
        },
        displayShipPlacementOptions2() {
            //checking if there are intersections in placed ships and displayed options
            for (i in this.objectForDisplayingSelectedShipsNoManipulation) {
                var shipLocation = this.objectForDisplayingSelectedShipsNoManipulation[i].locations[0]
                for (j in shipLocation) {
                    this.shipLocationsAsOneArray.push(shipLocation[j])
                }
            }
            console.log(this.shipLocationsAsOneArray, this.selectedShipType)

            for (l in this.verticalAxis){
                if(this.shipLocationsAsOneArray.includes(this.verticalAxis[l])){
                    console.log(this.shipLocationsAsOneArray)
                    this.verticalIntersection=true;
                }else{this.verticalIntersection=false;}
            }
            for (m in this.horisontalAxis){
                if(this.shipLocationsAsOneArray.includes(this.horisontalAxis[m])){
                    this.horizontalIntersection=true;
                }else{this.horizontalIntersection=false;}
            }
    
            for (l in this.verticalAxis) {
                if (this.lastCoordinate != 0 && this.verticalIntersection==false) {
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
            for (m in this.horisontalAxis) {
                if (this.lastCoordinate != 0 && this.horizontalIntersection==false) {
                    var img = document.createElement("img");
                    img.id = this.selectedShipType + m + 'hA'
                    img.src = "assets/" + this.selectedShipType + ".pur.png";
                    var src = document.getElementById(this.horisontalAxis[m] + "ps");
                    src.classList.remove("shake")
                    var shipToBeRemoved = document.getElementById(img.id);
                    shipToBeRemoved.parentNode.removeChild(shipToBeRemoved)
                    console.log("pic removed")
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
            console.log(this.verticalAxis.length, this.shipLength1)
            var shipLocationArray = []
            var verticalAxisReplacementArray = this.verticalAxis.slice();
            if (verticalAxisReplacementArray.length != this.shipLength1) {
                var coordinateString = []
                coordinateString = verticalAxisReplacementArray[0].split('');
                console.log(coordinateString)
                coordinateString = this.tableCols[this.tableCols.indexOf(coordinateString[0]) - 1] + coordinateString[1]
                console.log(coordinateString)
                verticalAxisReplacementArray.push(coordinateString)
            }
            console.log(this.horisontalAxis, verticalAxisReplacementArray)
            if (this.horisontalAxis.includes(this.lastCoordinate)) {
                shipLocationArray.push(this.horisontalAxis)
            } else {
                shipLocationArray.push(verticalAxisReplacementArray)
            }


            if (this.lastCoordinate != 0) {
                console.log("this.objectForDisplayingSelectedShips")
                this.objectForDisplayingSelectedShips.push({
                    "type": "" + this.selectedShipType,
                    "locations": shipLocationArray
                })
                this.objectForDisplayingSelectedShipsNoManipulation=this.objectForDisplayingSelectedShips
                this.counter++;
                console.log(this.counter)
                if (this.counter>4){
                    this.showConfirmButton=true;
                }else{this.showConfirmButton=false;}

                if (this.counter>=1){
                    this.showClearButton=true;
                }else{this.showClearButton=false;}

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
            console.log(this.objectForDisplayingSelectedShips)
            
        },

        displayShipsBeforePlacement() {
            for (i in this.objectForDisplayingSelectedShips) {
                var shipLocation = this.objectForDisplayingSelectedShips[i].locations[0]
                for (j in shipLocation) {
                    console.log(shipLocation[j])
                    document.getElementById(shipLocation[j] + "ps").style.backgroundColor = "thistle";
                    var img = document.createElement("img");
                    img.src = "assets/" + this.objectForDisplayingSelectedShips[i].type + ".pur.png";
                    var src = document.getElementById(shipLocation[j] + "ps");
                    src.appendChild(img);
                }
                this.objectForDisplayingSelectedShips = [];
            }
        },

        //after ships are placed
        makeGPShipsArray() {
            if (this.game[0].ships.length == 0) {
                this.shipsPlaced = false;
                console.log("no ships")
            } else {
                for (j in this.game[0].ships) {

                    for (i in this.game[0].ships[j].locations) {
                        this.shipLocations.push(this.game[0].ships[j].locations[i])
                    }
                }
                console.log("there are ships")
                this.fillArrDamagedShipLocations();
                this.displayMySalvoes();
            }

        },

        displayShips() {
            for (i in this.shipLocations) {
                document.getElementById(this.shipLocations[i]).style.backgroundColor = "thistle";
                var img = document.createElement("img");
                img.src = "assets/battleship.pur.png";
                var src = document.getElementById(this.shipLocations[i]);
                src.appendChild(img);
            }
            for (j in this.damagedShipLocations) {
                document.getElementById(this.damagedShipLocations[j]).innerHTML = '<i class="glyphicon glyphicon-remove" style="font-size:16px;color:purple;"></i>';
            }
        },

        fillArrDamagedShipLocations() {
            this.shipLocations.forEach(oneShipLocation => {
                this.enemySalvoes.map(oneEnemySalvo => {
                    if (oneEnemySalvo == oneShipLocation) {
                        this.damagedShipLocations.push(oneShipLocation)
                    }
                })
            })
            this.displayShips();
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
                document.getElementById(this.shipLocations[i] + "s").innerHTML = '<i class="glyphicon glyphicon-screenshot" style="font-size:17px;color:purple;background-color:thistle;"></i>';
                document.getElementById(this.shipLocations[i] + "s").style.backgroundColor = "thistle";
            }
        }
    },

    created: function () {
        this.fetchData();
    },
})