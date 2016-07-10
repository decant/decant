function name() {
    return "dominos coupon plugin";
}

function description() {
    return "Plugin to parse dominos offers and coupon messages.";
}

function filter(title) {
    if(title.toLowerCase().indexOf("09995342000") >= 0) {
        return true;
    } else {
        return false;
    }
}

function extractor(message) {
    var keyValuePairs = { };

    var offerText = /([^\n\r]*)[,;\.].*dominos\.co\.in/g;
    var offerText2 = /([^\n\r]*)[,;\.].*68886888/g;
    var offerCode = /.*dominos\.co\.in([^\n\r]*)T&C/g;
    var offerCode2 = /.*68886888\.([^\n\r]*)T&C/g;    
    var couponCode = /.*[c|C]pn:?([^\n\r]*)/g;
    var validity = /Vld:([^\n\r]*)/g;
    var coupon = /([^\n\r]*)[\.,]Vld:/g;
    var token = message.split(/;(.+)?/);
    
    message = token[1];

    var extractedOfferText = "";
    var extractedCode = "";
    var extractedCoupon = "";
    var extractedValidity = "---";

    if(message.toLowerCase().indexOf("dominos.co.in") > 0) {
        extractedCode = offerCode.exec(message);
        extractedOfferText = offerText.exec(message);        
    }
    else {
        extractedCode = offerCode2.exec(message);
        extractedOfferText = offerText2.exec(message);                
    }
    
    if(extractedCode[1].toLowerCase().indexOf("cpn") >= 0) {
        extractedCoupon = couponCode.exec(extractedCode[1])[1];
        
        if(extractedCoupon.toLowerCase().indexOf("vld") > 0) {
            var couponText = extractedCoupon;
            extractedValidity = validity.exec(couponText)[1];
            extractedCoupon = coupon.exec(couponText)[1];
            extractedValidity = "Upto " + extractedValidity;
        }
        else if(extractedOfferText[1].toLowerCase().indexOf("today") > 0) {
            extractedValidity = "Today Only";
        }
        else {
            extractedValidity = "N/A";
        }
        
    }
    else {
        extractedCoupon = extractedCode[1];

        if(extractedOfferText[1].toLowerCase().indexOf("today") > 0) {
            extractedValidity = "Today Only";
        }
        else {
            extractedValidity = "N/A --";
        }        
    }
    
    
    keyValuePairs.offerText = extractedOfferText[1];
    keyValuePairs.offerCode = extractedCoupon;
    keyValuePairs.offerValidity = extractedValidity;
    
    return JSON.stringify(keyValuePairs);
}

function filterKeys() {
    var keys = { } ;

    keys.key1 = "offerText";
    keys.key2 = "offerValidity";

    return JSON.stringify(keys);
}


function settings() {
    var settings = { } ;

    settings.display_name = { };
    settings.display_name.offerText = "Offer Type";
    settings.display_name.offerCode = "Coupon Code";
    settings.display_name.offerValidity = "Validity";

    settings.is_displayed = { };
    settings.is_displayed.offerText = "true";
    settings.is_displayed.offerCode = "true";
    settings.is_displayed.offerValidity = "true";

    return JSON.stringify(settings);
}

