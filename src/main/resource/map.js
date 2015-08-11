const
period = 1000; // time between refreshes in ms
var mapOptions = {
	center : {
		lat : 0,
		lng : 0
	},
	zoom : 8,
	mapTypeId : google.maps.MapTypeId.TERRAIN
};
var map;
var polyOptions = {
	geodesic : true,
	strokeColor : '#000000',
	strokeOpacity : 1.0,
	strokeWeight : 2
};
var markerOptions = {
	/*
	 * icon: { path:
	 * "M250.2,59.002c11.001,0,20.176,9.165,20.176,20.777v122.24l171.12,95.954v42.779l-171.12-49.501v89.227l40.337," +
	 * "29.946v35.446l-60.52-20.18-60.502,20.166v-35.45l40.341-29.946v-89.227l-171.14,49.51v-42.779l171.14-95.954v-" +
	 * "122.24c0-11.612,9.15-20.777,20.16-20.777z", scale: 0.1, fillOpacity: 1,
	 * anchor: new google.maps.Point(250,250), strokeWeight: 0.5 }
	 */
	icon : {
		path : "M362.985,430.724l-10.248,51.234l62.332,57.969l-3.293,26.145 l-71.345-23.599l-2.001,13.069l-2.057-13.529l-71.278,"
				+ "22.928l-5.762-23.984l64.097-59.271l-8.913-51.359l0.858-114.43 l-21.945-11.338l-189.358,"
				+ ""
				+ "88.76l-1.18-32.262l213.344-180.08l0.875-107.436l7.973-32.005l7.642-12.054l7.377-3.958l9.238,"
				+ "3.65 l6.367,14.925l7.369,30.363v106.375l211.592,182.082l-1.496,32.247l-188.479-90.61l-21.616,10.087l-0.094,115.684",
		scale : 0.08,
		fillOpacity : 1,
		anchor : new google.maps.Point(250, 250),
		strokeWeight : 0.5
	}
};

var iconAirport = {
	url : 'airport.png',
	size : new google.maps.Size(30, 50),
	origin : new google.maps.Point(0, 0),
	anchor : new google.maps.Point(15, 50)
};

var iconWaypoint = {
	path : google.maps.SymbolPath.CIRCLE,
	scale : 6,
	strokeColor : '#0000FF',
	strokeOpacity : 1,
	strokeWeight : 3,
	fillColor : 'white',
	fillOpacity : 1,
};

var planeList = {};
var refreshControlPanel = false;
var planeToFollow = null;
var colors = [ "#26764E", "#F08526", "#9CFF54", "#721B49", "#A7D8F8",
		"#2AFDBC", "#FBE870", "#711302", "#2572C2", "#1C271D", "#632E85",
		"#1E5F7A", "#D8B2F5", "#D307A2", "#F391B5", "#F180F5", "#3A1E2E",
		"#AE7707", "#3E3D0E", "#6AB06E" ];
var color_index = 0;
var navMap;
var flightPlan = {};
var flightPath; // an object google.maps.Polyline - representing the Flight Plan
var flightPlanCoordinates; // array of google.maps.LatLng

$.ajaxSetup({
	cache : false
}); // else IE caches the data request...

function initialize() {

	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

	google.maps.event.addListener(map, 'dragstart', function() {
		$('#nofocus').click();
	});

	$('body').keyup(function(e) {
		if (e.keyCode == 9) {
			if ($('#panel').is(":hidden")) {
				showPanel();
			} else
				hidePanel();
		}
		if (e.keyCode == 78) {
			if (navMap.getOpacity()) {
				hideNavaids();
			} else
				showNavaids();
		}
	});

	updatePosition();
	setInterval(updatePosition, period);
	// load initial plane data and place planes

	// nav data overlays
	navMap = new google.maps.ImageMapType({
		getTileUrl : function(coord, zoom) {
			tileSize = 256 / Math.pow(2, zoom);
			west = coord.x * tileSize;
			east = west + tileSize;
			north = coord.y * tileSize;
			south = north + tileSize;

			northEast = map.getProjection().fromPointToLatLng(
					new google.maps.Point(east, north));
			southWest = map.getProjection().fromPointToLatLng(
					new google.maps.Point(west, south));

			// http://x-plane-map.fouc.net/nav.php?north=-24.5271%20&south=-25.7999%20&east=-46.4063%20&west=-47.8125
			// console.log();

			return [ 'http://x-plane-map.fouc.net/nav.php?north=',
					northEast.lat().toFixed(4), '&south=',
					southWest.lat().toFixed(4), '&east=',
					northEast.lng().toFixed(4), '&west=',
					southWest.lng().toFixed(4) /*
												 * , '&type=APT'
												 */].join('');
		},
		tileSize : new google.maps.Size(256, 256),
		minZoom : 6,
		maxZoom : 12
	});

	map.overlayMapTypes.push(navMap);
	navMap.setOpacity(0);

	loadFlightPlan();
}


function loadFlightPlanData() {
	// Loading Flight Plan Information
	flightPlan = {
		departure : {
			id : "SBSP",
			name : "Congonhas",
			latlng : new google.maps.LatLng(-23.62611, -46.656387)
		},
		waypoints : [ {
			id : "LODOG",
			latlng : new google.maps.LatLng(-23.545668, -45.339333)
		}, {
			id : "XOKIX",
			latlng : new google.maps.LatLng(-23.142668, -43.52017)
		} ],
		destination : {
			id : "SBRJ",
			name : "Santos Dumont",
			latlng : new google.maps.LatLng(-22.91, -43.162777)
		}
	}
}

function loadFlightPlan() {
	loadFlightPlanData();
	
	// Loading flightPlanCoordinates variable
	var arrCoord = new Array();
	arrCoord[0] = flightPlan.departure.latlng;
	var totalWaypoints = 0;
	while (totalWaypoints < flightPlan.waypoints.length) {
		arrCoord[totalWaypoints + 1] = flightPlan.waypoints[totalWaypoints].latlng;
		// Mark the Waypoint
		waypoint = {
				id : flightPlan.waypoints[totalWaypoints].id,
				latlng : flightPlan.waypoints[totalWaypoints].latlng
		}
		markWaypoint(waypoint);
		
		totalWaypoints++;
	}
	arrCoord[totalWaypoints + 1] = flightPlan.destination.latlng;
	flightPlanCoordinates = arrCoord;
	
	// Loading Flight Plan Polyline - Draw the line
	flightPath = new google.maps.Polyline({
		path : flightPlanCoordinates,
		geodesic : true,
		strokeColor : '#0000FF',
		strokeOpacity : 0.5,
		strokeWeight : 6
	});
	flightPath.setMap(map);
	
	// Mark for the Airport Departure
	departure = {
		id  : flightPlan.departure.id,
		name : flightPlan.departure.name,
		latlng : flightPlan.departure.latlng 
	}
	markAirport(departure);
	
	// Mark for the Airport Destination
	destination = {
		id  : flightPlan.destination.id,
		name : flightPlan.destination.name,
		latlng : flightPlan.destination.latlng 
	}
	markAirport(destination);
	
	var panFlightPlan = new google.maps.LatLngBounds(flightPlan.departure.latlng, flightPlan.destination.latlng);
	map.fitBounds(panFlightPlan);
}

function markAirport(airport) {
	var m1 = new MarkerWithLabel({
		position : airport.latlng,
		animation : google.maps.Animation.DROP,
		icon : iconAirport,
		labelContent : airport.id,
		labelAnchor : new google.maps.Point(20, 70),
		labelClass : "labelsAirport"
	});
	var infoM1 = new google.maps.InfoWindow({
		content : "<b>" + airport.id + "</b> - " + airport.name
	});
	google.maps.event.addListener(m1, "click", function(e) {
		infoM1.open(map, this);
	});
	m1.setMap(map);
}

function markWaypoint(waypoint) {
	var mw1 = new MarkerWithLabel({
		position : waypoint.latlng,
		animation : google.maps.Animation.DROP,
		icon : iconWaypoint,
		labelContent : waypoint.id,
		labelAnchor : new google.maps.Point(28, -6),
		labelClass : "labelsWaypoint"
	});
	mw1.setMap(map);
}

function updatePosition() {
	$
			.getJSON(
					"data",
					function(data) {
						if ($.isEmptyObject(data)) {
							showError("No planes were detected. Please check X-Plane's data output and internet settings,"
									+ " and make sure that everyone's firewall allows inbound and outbound UDP traffic to port 49003.");
						}

						// delete all absent planes
						for ( var ip in planeList) {
							if (!(ip in data)) {
								deletePlane(ip);
								refreshControlPanel = true;
							}
						}

						// for current and new planes
						for ( var ip in data) {

							// if new plane
							if (!(ip in planeList)) {
								color = nextColor();

								markerOptions.icon.fillColor = color;
								planeList[ip] = {
									name : ip.replace(/-/g, '.'),
									lon : 0,
									lat : 0,
									alt : data[ip].alt,
									marker : new google.maps.Marker(
											markerOptions),
									trace : new google.maps.Polyline(
											polyOptions),
									info : new google.maps.InfoWindow(),
									color : color

								};
								// setup marker
								planeList[ip].marker.setMap(map);
								planeList[ip].marker.ip = ip; // this is
								// necessary for
								// the browser
								// to know which
								// info window
								// to open
								// setup event
								planeList[ip].infoWindowListener = google.maps.event
										.addListener(
												planeList[ip].marker,
												'click',
												function() {
													planeList[this.ip].info
															.open(
																	map,
																	planeList[this.ip].marker);
												});

								// setup polyline
								planeList[ip].trace.setMap(map);

								planeToFollow = ip;
								refreshControlPanel = true;
							}

							newLat = data[ip].lat;
							newLon = data[ip].lon;
							planeList[ip].alt = data[ip].alt;

							var newPoint = new google.maps.LatLng(newLat,
									newLon);

							// set marker position to new point
							planeList[ip].marker.setPosition(newPoint);

							// rotate marker
							hdg = bearing(planeList[ip].lon, planeList[ip].lat,
									newLon, newLat);
							var icon = planeList[ip].marker.getIcon();
							icon.rotation = hdg;
							planeList[ip].marker.setIcon(icon);

							// calculate speed
							spd = distance(planeList[ip].lon,
									planeList[ip].lat, newLon, newLat)
									/ (period / 1000) * 3600 / 1.852;

							// add new point to line
							planeList[ip].trace.getPath().push(newPoint);

							// set info window content
							planeList[ip].info
									.setContent('<div style="margin: 0; width: 150px;"><strong>'
											+ planeList[ip].name
											+ '</strong><br>'
											+ planeList[ip].alt.toFixed()
											+ ' ft MSL / '
											+ (hdg + 360).toFixed()
											% 360
											+ '&deg;<br>'
											+ 'GS '
											+ spd.toFixed() + ' kts</div>');

							// set table content
							$('.planeRow[data-ip="' + ip + '"] .altText').html(
									planeList[ip].alt.toFixed() + ' ft');
							$('.planeRow[data-ip="' + ip + '"] .hdgText').html(
									(hdg + 360).toFixed() % 360 + '&deg;');
							$('.planeRow[data-ip="' + ip + '"] .spdText').html(
									'GS ' + spd.toFixed() + ' kts');

							// save plane data
							planeList[ip].lon = newLon;
							planeList[ip].lat = newLat;
							planeList[ip].hdg = hdg;
							planeList[ip].spd = spd;
						}

						// move map if checkbox checked
						if (planeToFollow != null)
							map.panTo(new google.maps.LatLng(
									planeList[planeToFollow].lat,
									planeList[planeToFollow].lon));

						if (refreshControlPanel) {
							refreshCP();
						}

					})
			.error(
					function() {
						showError('There seems to be an issue with the script, is it still running ?')
					});

}

function bearing(lon1, lat1, lon2, lat2) {
	lon1 = lon1 * Math.PI / 180;
	lon2 = lon2 * Math.PI / 180;
	lat1 = lat1 * Math.PI / 180;
	lat2 = lat2 * Math.PI / 180;

	var y = Math.sin(lon2 - lon1) * Math.cos(lat2);
	var x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2)
			* Math.cos(lon2 - lon1);
	var brng = Math.atan2(y, x);

	return brng / Math.PI * 180;
}

function distance(lon1, lat1, lon2, lat2) {
	R = 6372.8;
	lat1 = lat1 * Math.PI / 180;
	lat2 = lat2 * Math.PI / 180;
	var deltalat = lat2 - lat1;
	var deltalon = (lon2 - lon1) * Math.PI / 180;

	var a = Math.sin(deltalat / 2) * Math.sin(deltalat / 2) + Math.cos(lat1)
			* Math.cos(lat2) * Math.sin(deltalon / 2) * Math.sin(deltalon / 2);
	var c = 2 * Math.asin(Math.sqrt(a));

	// console.log(R*c);
	return R * c; // returns kilometers
}

// clean plane deletion
function deletePlane(ip) {
	google.maps.event.removeListener(planeList[ip].infoWindowListener);
	planeList[ip].trace.setMap(null);
	planeList[ip].marker.setMap(null);
	delete planeList[ip];
}

// refresh control panel
function refreshCP() {
	$('.planeRow').remove();
	for ( var ip in planeList) {
		$("#planesTable")
				.append(
						'<tr class="planeRow'
								+ ((planeToFollow == ip) ? ' followed' : '')
								+ '" data-ip="'
								+ ip
								+ '">'
								+ '<td style="background-color: '
								+ planeList[ip].color
								+ ';"title="Click to focus on this plane."><label><input type="radio" name="plane"></label></td>'
								+ '<td title="Double-click to rename.">'
								+ '<strong class="plane-name">'
								+ planeList[ip].name
								+ '</strong><br>'
								+ '<span class="altText">'
								+ planeList[ip].alt.toFixed()
								+ ' ft</span> | <span class="hdgText">'
								+ (planeList[ip].hdg + 360).toFixed() % 360
								+ '&deg;</span> | <span class="spdText">GS '
								+ planeList[ip].spd.toFixed()
								+ ' kts</span>'
								+ '</td>'
								+ '<td title="Click to show or hide trace."><input type="checkbox" class="trace-show" checked></td>'
								+ '<td title="Click to reset the plane\'s trace."><button class="trace-clear">Clr</button></td>'
								+ '<td title="Click to remove the plane."><button class="plane-remove" >Rm</button></td>'
								+ '</tr>');
	}

	// resetting js events
	// radio button
	$('input[name=plane]').change(
			function() {
				$('.planeRow').removeClass("followed");
				$('input[name=plane]:checked').parents('tr').addClass(
						"followed");
				ip = $('input[name=plane]:checked').parents('tr').data("ip");
				planeToFollow = ip;
				if (ip != null)
					map.panTo(new google.maps.LatLng(planeList[ip].lat,
							planeList[ip].lon));
			});

	// hide/show trace checkbox
	$('.trace-show').change(function() {
		ip = $(this).parents('.planeRow').data("ip");
		planeList[ip].trace.setVisible($(this).is(':checked'));
	});

	// trace clear button
	$('.trace-clear').click(
			function() {
				ip = $(this).parents('.planeRow').data("ip");
				planeList[ip].trace.setMap(null);
				planeList[ip].trace = new google.maps.Polyline(polyOptions);
				planeList[ip].trace.setMap(map);
				planeList[ip].trace.getPath().push(
						new google.maps.LatLng(planeList[ip].lat,
								planeList[ip].lon));
			});

	// plane remove button

	$('.plane-remove').click(function() {
		if (confirm('Are you sure you want to remove this plane ?')) {
			ip = $(this).parents('.planeRow').data("ip");
			deletePlane(ip);
			refreshCP();
		}
	});

	// plane name edition
	$('.plane-name').dblclick(function() {
		ip = $(this).parents('.planeRow').data("ip");
		theName = planeList[ip].name;
		$(this).replaceWith($('<input/>', {
			value : theName,
			id : 'planeNameInput',
			'data-ip' : ip
		}).val(theName));

		$('#planeNameInput').select().keyup(function(e) {
			if (e.keyCode == 13) {
				theNewName = $(this).val();
				theIP = $(this).data('ip');
				planeList[theIP].name = theNewName;
				refreshCP();
			} else if (e.keyCode == 27) {
				refreshCP();
			}
		});
	});

	$('#planesTable tr td:nth-child(2)').click(function() {
		$(this).parents('tr').find('input[name=plane]').click();
	});

	refreshControlPanel = false;

}

// alert() equivalent
function showError(text) {
	$('#errorBox').text(text);
	$('#errorBox').fadeIn().delay(3500).fadeOut();
}

function nextColor() {
	if (colors[color_index] != null) {
		var color = colors[color_index];
		color_index++;
		return color;
	} else {
		console.log("No more colors");
		return "#aaaaaa";
	}
}

function hidePanel() {
	$('#panel').animate(
			{
				'right' : '-300px'
			},
			400,
			'swing',
			function() {
				$('#panel-button').html('Show panel (Tab)').unbind('click')
						.click(showPanel);
				$('#panel').hide();
				google.maps.event.trigger(map, 'resize');
			});
	$('#map-canvas-wrapper').animate({
		'margin-right' : '0px'
	});
}

function showPanel() {
	$('#panel').show();
	$('#panel').animate(
			{
				'right' : '0px'
			},
			400,
			'swing',
			function() {
				$('#panel-button').html('Hide panel (Tab)').unbind('click')
						.click(hidePanel);
				google.maps.event.trigger(map, 'resize');
			});
	$('#map-canvas-wrapper').animate({
		'margin-right' : '300px'
	});
}

function hideNavaids() {
	$('#navaids-button').html('Show navaids (N)').unbind('click').click(
			showNavaids);
	navMap.setOpacity(0);
}

function showNavaids() {
	$('#navaids-button').html('Hide navaids (N)').unbind('click').click(
			hideNavaids);
	navMap.setOpacity(1);
}

// ready when you are!
google.maps.event.addDomListener(window, 'load', initialize);