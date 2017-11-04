"use strict";

function bet() {
    var id = document.getElementById("idInput").value;
    var bet = document.getElementById("betInput").value;
    sendHttpRequest("GET", "bet?id=" + id + "&bet=" + bet, null, display);
}

function getPlayer() {
    var id = document.getElementById("getId").value;
    sendHttpRequest("GET", "player?id=" + id, null, display);
}

function addPlayer() {
    var id = document.getElementById("addId").value;
    var balance = document.getElementById("addBalance").value;
    sendHttpRequest("POST", "player?id=" + id + "&balance=" + balance, null, display);
}


function display(json) {
    document.getElementById("msg").innerHTML = json;
}


function sendHttpRequest(action, url, data, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            callback(xmlHttp.responseText);
        }
    };
    xmlHttp.open(action, url, true);
    xmlHttp.send(data);
}
