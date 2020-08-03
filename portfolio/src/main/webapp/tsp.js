let map;

function createMap() {
  map = new google.maps.Map(
    $("#map")[0],
    {center: {lat: 49.988358, lng: 36.232845}, zoom: 10}
  );

  map.addListener("click", event => addMarker(event.latLng));
}

let markers = [];

function addMarker(latLng) {
    if (markers.length == 10) {
        alert("You can't enter more than 10 points");
        return;
    }

    let marker = new google.maps.Marker({position: latLng, map: map});

    marker.addListener("click", _ => {
        marker.setMap(null);
        markers.splice(markers.indexOf(marker), 1);
    });

    markers.push(marker);
    removePath();
}

function clearMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers = [];
    removePath();
}

let shortestPath;

function removePath() {
    if (shortestPath) {
        shortestPath.setMap(null);
    }
}

async function drawShortestPath() {

    removePath();

    $("#buttons button").attr("disabled", true);

    let encodedPath = await fetch("/solve-tsp", {
        method: "POST",
        body: JSON.stringify(markers.map(x => x.position))
    })
        .then(response => response.text());

    let path = google.maps.geometry.encoding.decodePath(encodedPath);

    shortestPath = new google.maps.Polyline({path: path, map: map});

    $("#buttons button").attr("disabled", false);
}

window.onload = () => {
    let gmaps = document.createElement("script");

    gmaps.src = `https://maps.googleapis.com/maps/api/js?key=${config.GMAPS_API_KEY}&libraries=geometry`;
    gmaps.onload = createMap;

    document.head.appendChild(gmaps);
};
