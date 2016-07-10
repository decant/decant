/**
 * Name of the plugin
 */
function name() {
    return "amazon status plugin";
}

function description() {
    return "Plugin to parse various amazon order status messages.";
}

function filter(title) {
    if(title.toLowerCase().indexOf("09995341000") >= 0) {
        return true;
    } else {
        return false;
    }
}

function extractor(message) {
    var keyValuePairs = { };

    var orderPlacedRegEx = /.*Your order for\s*([^\n\r]*)has been successfully/g;
    
    var dispatchedAmazon = /.*Your package with\s*([^\n\r]*)will be delivered/g;
    var dispatchedAmazonDate = /.*on or before\s*([^\n\r]*)\. /g;
    var dispatchedAmazonTrackingURL = /.*Track at\s*([^\n\r]*)/g;

    var dispatchedOther = /.*Your package with\s*([^\n\r]*)has been [shipped|dispatched]/g;
    var dispatchedOtherTracking = /.*has been shipped via\s*([^\n\r]*)[\.|\s]Delivery/g;
    var dispatchedOtherDate = /.*Delivery Estimate([^\n\r]*)/g;
    
    var dispatchedOtherDate2 = /.*Expected delivery between:([^\n\r]*)\. /g;

    var arrivingItem = /.*Your package with([^\n\r]*)is out for delivery/g;
    var arrivingTracking = /.*Track([^\n\r]*)/g;

    var deliveredItem = /.*Your package with([^\n\r]*)was successfully delivered/g;
    var deliveredTracking = /.*More info at([^\n\r]*)/g;
    
    message = message.trim();
    
    if(message.toLowerCase().indexOf("your order") == 0) {
        var extractedName = orderPlacedRegEx.exec(message);
        
        keyValuePairs.itemName = extractedName[1];
        keyValuePairs.itemTrackingStatus = "N/A";
        keyValuePairs.itemExpectedDelivery = "Not Shipped";
    }

    if(message.toLowerCase().indexOf("dispatched") == 0) {
        var extractedName = dispatchedAmazon.exec(message);
        var extractedDate = dispatchedAmazonDate.exec(message);
        var extractedTrackingURL = dispatchedAmazonTrackingURL.exec(message);
        
        keyValuePairs.itemName = extractedName[1];
        keyValuePairs.itemTrackingStatus = "Track at " + extractedTrackingURL[1];
        keyValuePairs.itemExpectedDelivery = "Before " + extractedDate[1];       
    }
    else if (message.toLowerCase().indexOf("your package") == 0) {
        if (message.toLowerCase().indexOf("dispatched by the seller") > 0) {
            var extractedName = dispatchedOther.exec(message);
            var extractedDate = dispatchedOtherDate2.exec(message);
            
            keyValuePairs.itemName = extractedName[1];
            keyValuePairs.itemTrackingStatus = "Check Email";
            keyValuePairs.itemExpectedDelivery = "Between " + extractedDate[1];
        }
        else {
            var extractedName = dispatchedOther.exec(message);
            var extractedDate = dispatchedOtherDate.exec(message);
            var extractedTrackingURL = dispatchedOtherTracking.exec(message);
            
            keyValuePairs.itemName = extractedName[1];
            keyValuePairs.itemTrackingStatus = extractedTrackingURL[1];
            keyValuePairs.itemExpectedDelivery = "Between " + extractedDate[1];
        }
    }

    if(message.toLowerCase().indexOf("arriving today") == 0) {
        var extractedName = arrivingItem.exec(message);        
        var extractedTrackingURL = arrivingTracking.exec(message);

        keyValuePairs.itemName = extractedName[1];
        keyValuePairs.itemTrackingStatus = "Track at " + extractedTrackingURL[1];
        keyValuePairs.itemExpectedDelivery = "Arriving Soon"; 
    }

    if(message.toLowerCase().indexOf("delivered") == 0) {
        var extractedName = deliveredItem.exec(message);        
        var extractedTrackingURL = deliveredTracking.exec(message);

        keyValuePairs.itemName = extractedName[1];
        keyValuePairs.itemTrackingStatus = "Track at " + extractedTrackingURL[1];
        keyValuePairs.itemExpectedDelivery = "Delivered"; 
    }
    
    return JSON.stringify(keyValuePairs);
}

function filterKeys() {
    var keys = { } ;

    keys.key1 = "itemTrackingStatus";
    keys.key2 = "itemName";

    return JSON.stringify(keys);
}


function settings() {
    var settings = { } ;

    settings.display_name = { };
    settings.display_name.itemName = "Name";
    settings.display_name.itemTrackingStatus = "Tracking";
    settings.display_name.itemExpectedDelivery = "Delivery";

    settings.is_displayed = { };
    settings.is_displayed.itemName = "true";
    settings.is_displayed.itemTrackingStatus = "true";
    settings.is_displayed.itemExpectedDelivery = "true";

    return JSON.stringify(settings);
}

