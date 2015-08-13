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

var iconVOR = {
	path : google.maps.SymbolPath.CIRCLE,
	scale : 7,
	strokeColor : '#000000',
	strokeOpacity : 1,
	strokeWeight : 3,
	fillColor : 'yellow',
	fillOpacity : 1,
};

var iconNDB = {
	path : google.maps.SymbolPath.CIRCLE,
	scale : 7,
	strokeColor : '#262626',
	strokeOpacity : 1,
	strokeWeight : 3,
	fillColor : '#FFD05C',
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
		if (e.keyCode == 78) {
			if (navMap.getOpacity()) {
				hideNavaids();
			} else
				showNavaids();
		}
		if (e.keyCode == 16) {
			toggleFlightPanel();
		}
	});

	updatePosition();
	setInterval(updatePosition, period);
	setInterval(checkFlightPlanBoxAndLoad, period);
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
}

var flightplanText = 'none';
function checkFlightPlanBoxAndLoad() {
	if ($('#panel-fp').is(":hidden") == false) {
		var text = $('textarea#boxFlightPlan').val();
		if (text != flightplanText) {
			flightplanText = text;
			$
					.getJSON(
							"flightplan",
							function(data) {
								if ($.isEmptyObject(data)) {
									showError("Error loading Flight Plan. No content was returned.");
								}
								flightPlan = data;
								loadFlightPlan();
							}

					)
					.error(
							function() {
								showError('Please check the connection with http://server:port/flightplan, is not working.')
							});
		}
	}
}

function loadFlightPlanDataFake() {
	// Loading Flight Plan Information
	/*flightPlan = {
		departure : {
			id : "SBSP",
			name : "Congonhas",
			latlng : new google.maps.LatLng(-23.62611, -46.656387),
			runways : [ {
				number : "17R",
				heading : "168",
				length : 3.500,
				frenquency : "120.00",
				elevation : 2.300
			}, {
				number : "35R",
				heading : "18",
				length : 3.500,
				frenquency : "140.20",
				elevation : 2.300
			} ]
		},
		waypoints : [ {
			id : "BCO",
			latlng : new google.maps.LatLng(-23.406428, -46.385464),
			type : 1,
			descr : "BONSUCESSO SAO PAULO",
			freq : "116.00"
		}, {
			id : "TBE",
			latlng : new google.maps.LatLng(-23.045636, -45.516708),
			type : 2,
			descr : "TAUBATE",
			freq : "430"
		}, {
			id : "LODOG",
			latlng : new google.maps.LatLng(-23.545668, -45.339333)
		}, {
			id : "XOKIX",
			latlng : new google.maps.LatLng(-23.142668, -43.52017)
		} ],
		destination : {
			id : "SBRJ",
			name : "Santos Dumont",
			latlng : new google.maps.LatLng(-22.91, -43.162777),
			runways : [ {
				number : "15L",
				heading : "168",
				length : 2.500,
				frenquency : "120.00",
				elevation : 300
			}, {
				number : "20L",
				heading : "18",
				length : 2.500,
				frenquency : "140.20",
				elevation : 300
			} ]
		}
	}*/
}

function loadFlightPlan() {
	// Loading flightPlanCoordinates variable
	var departureLatLng = new google.maps.LatLng(flightPlan.departure.latitude, flightPlan.departure.longitude);
	var destinationLatLng = new google.maps.LatLng(flightPlan.destination.latitude, flightPlan.destination.longitude); 
	var arrCoord = new Array();
	arrCoord[0] = departureLatLng;
	var totalWaypoints = 0;
	while (totalWaypoints < flightPlan.waypoints.length) {
		arrCoord[totalWaypoints + 1] = 
			new google.maps.LatLng(
					flightPlan.waypoints[totalWaypoints].latitude,flightPlan.waypoints[totalWaypoints].longitude
			);
		// Mark the Waypoint
		waypoint = {
			id : flightPlan.waypoints[totalWaypoints].id,
			latlng : new google.maps.LatLng(flightPlan.waypoints[totalWaypoints].latitude,flightPlan.waypoints[totalWaypoints].longitude),
			type : flightPlan.waypoints[totalWaypoints].type,
			descr : flightPlan.waypoints[totalWaypoints].name,
			freq : flightPlan.waypoints[totalWaypoints].frequency
		}
		markWaypoint(waypoint);

		totalWaypoints++;
	}
	arrCoord[totalWaypoints + 1] = destinationLatLng;
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
		id : flightPlan.departure.icaoId,
		name : flightPlan.departure.name,
		latlng : departureLatLng,
		runways : flightPlan.departure.arrayRunways
	}
	markAirport(departure);

	// Mark for the Airport Destination
	destination = {
		id : flightPlan.destination.icaoId,
		name : flightPlan.destination.name,
		latlng : destinationLatLng,
		runways : flightPlan.destination.arrayRunways
	}
	markAirport(destination);

	var panFlightPlan = new google.maps.LatLngBounds(departureLatLng, destinationLatLng);
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

	var infoContent = "<b>" + airport.id + "</b> - " + airport.name + "<hr/>";
	infoContent += "<table width='100%' class='runwayTable' cellspacing='0' cellpadding='0'>";
	for (var i = 0; i < airport.runways.length; i++) {
		infoContent += "<tr>"
				+ " <td>Runway <span class='runwayInfo'>"
				+ airport.runways[i].number
				+ "</span></td>"
				+ " <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Heading: <span class='runwayInfo'>"
				+ airport.runways[i].heading
				+ "<font size='2px'>&deg;</font></span></td>"
				+ " <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Frequency: <span class='runwayInfo'>"
				+ airport.runways[i].frequency + "</span><br/></td>" + "</tr>";
	}
	infoContent += "</table>";

	var infoM1 = new google.maps.InfoWindow({
		content : infoContent
	});
	google.maps.event.addListener(m1, "mouseover", function(e) {
		infoM1.open(map, this);
	});
	google.maps.event.addListener(m1, "mouseout", function(e) {
		infoM1.close();
	});
	m1.setMap(map);
}

function markWaypoint(waypoint) {
	var iconWPT = iconWaypoint;
	var infoContent = "";
	if (waypoint.type == 1) {
		iconWPT = iconVOR;
	} else if (waypoint.type == 2) {
		iconWPT = iconNDB;
	}

	var mw1 = new MarkerWithLabel({
		position : waypoint.latlng,
		animation : google.maps.Animation.DROP,
		icon : iconWPT,
		labelContent : waypoint.id,
		labelAnchor : new google.maps.Point(28, -6),
		labelClass : "labelsWaypoint"
	});
	if (waypoint.type == 'VOR' || waypoint.type == 'NDB') {
		// VOR and NDB
		infoContent = "<table border=0 class='vorTable' cellspacing='0' cellpadding='0' width='230px'>";
		infoContent += "<tr>";
		infoContent += " <td valign='middle' colspan=2>";
		infoContent += "  <table border=0 class='vorTable' cellspacing='0' cellpadding='0'><tr><td>";
		if (waypoint.type == 'VOR') {
			// VOR
			infoContent += " <img src='VOR.png'/>";
		} else if (waypoint.type == 'NDB') {
			// NBD
			infoContent += " <img src='NDB.png'/>";
		}
		infoContent += "</td>";
		infoContent += "<td>&nbsp;&nbsp;&nbsp;<b>" + waypoint.id + "</b> - "
				+ waypoint.descr + "</td>" + "</tr></table>"
		infoContent += "<tr><td colspan='2'><hr/></td></tr>";
		infoContent += "<tr>"
				+ " <td width='20%'> Frequency:&nbsp;</td><td><span class='vorInfo'>"
				+ waypoint.freq + "</td>" + "</tr>";
		infoContent += "<tr>"
				+ " <td width='1%'> Latitude:&nbsp;</td><td><span class='vorInfo'>"
				+ precisionDecimalNumber(waypoint.latlng.lat()) + "</td>"
				+ "</tr>";
		infoContent += "<tr>"
				+ " <td> Longitude:&nbsp;</td><td><span class='vorInfo'>"
				+ precisionDecimalNumber(waypoint.latlng.lng()) + "<br/></td>"
				+ "</tr>";
		infoContent += "</table>";
	} else {
		// FIX
		infoContent = "<table border=0 class='vorTable' cellspacing='0' cellpadding='0' width='120px'>";
		infoContent += "<td colspan=2><b>" + waypoint.id + "</b></td></tr>"
		infoContent += "<tr><td colspan='2'><hr/></td></tr>";
		infoContent += "<tr>"
				+ " <td width='1%'> Latitude:&nbsp;</td><td><span class='vorInfo'>"
				+ precisionDecimalNumber(waypoint.latlng.lat()) + "</td>"
				+ "</tr>";
		infoContent += "<tr>"
				+ " <td> Longitude:&nbsp;</td><td><span class='vorInfo'>"
				+ precisionDecimalNumber(waypoint.latlng.lng()) + "<br/></td>"
				+ "</tr>";
		infoContent += "</table>";
	}
	var infoM1 = new google.maps.InfoWindow({
		content : infoContent
	});
	/*
	 * google.maps.event.addListener(mw1, "click", function(e) {
	 * infoM1.open(map, this); });
	 */
	google.maps.event.addListener(mw1, "mouseover", function(e) {
		infoM1.open(map, this);
	});
	google.maps.event.addListener(mw1, "mouseout", function(e) {
		infoM1.close();
	});
	mw1.setMap(map);
}

function precisionDecimalNumber(vlr) {
	return parseFloat(Math.round(vlr * 100000) / 100000).toFixed(5);
	;
}

function updatePosition() {
	$
			.getJSON(
					"data",
					function(data) {
						if ($.isEmptyObject(data)) {
							showError("No planes detected at X-Plane's UDP traffic port 49003. "
									+ "Please check the settings at the X-Plane's Net Connections menu.");
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

function toggleFlightPanel() {
	if ($('#panel-fp').is(":hidden")) {
		showFlightPanel();
	} else {
		hideFlightPanel();
	}
}
function hideFlightPanel() {
	$('#panel-fp').hide(500);
	$('#flightplan-help').hide(300);
	$('#flightplan-button').removeClass("down").addClass("up");
}

function showFlightPanel() {
	$('#panel-fp').show(500);
	$('#flightplan-help').show(300);
	$('#flightplan-button').removeClass("up").addClass("down");
	$('#boxFlightPlan').focus();
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