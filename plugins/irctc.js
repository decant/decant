/**
 * Name of the plugin
 */
function name() {
    return "irctc plugin";
}

/**
 * A brief description of the plugin, and what it does
 */
function description() {
    return "Plugin to parse IRCTC SMS messages";
}

/**
 * Filter function takes in the SMS sender and checks if the sender has to be
 * filtered, if the sender matches a specific vendor the user wishes to
 * handle then a true should be returned, only then the rest of the plugin
 * gets executed, if a false is returned the message is ignored.
 *
 * @param {String} title - The SMS Sender in a string format
 * @returns {Boolean} - If set to true, rest of the filter plugin runs
 */
function filter(title) {
    if(title.toLowerCase().indexOf("09995340000") >= 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * Extractor function takes in the SMS Body and then the user is able extract
 * all the necessary information from the SMS Body and convert them into
 * meaningful key-value pairs, which is the returned back as a stringified JSON.
 * This string will be used for processing inside the filtering application.
 *
 * @param (String) message - The RAW unfiltered SMS Body
 * @returns {String} - The Strigified JSON of the parsed Body in form of
 *                     Key-Value pairs
 */
function extractor(message) {
    var tokens = message.split(",");
    var keyValuePairs = { };

    var pnrData = tokens[0].split(":");
    var trainData = tokens[1].split(":");
    var personName = tokens[6];
    var ticketStatus = tokens[7];

    keyValuePairs.pnr = pnrData[1];
    keyValuePairs.train = trainData[1];
    keyValuePairs.name = personName;
    keyValuePairs.status = ticketStatus;

    return JSON.stringify(keyValuePairs);
}

/**
 * Filter Keys are the set of keys from the Key-Value pair generated by the
 * extractor which can filter the data, so that the user can view all of this in
 * a more sensible way. The values of the filter should match the key string
 * mentioned in the extractor function, this data is retured back as a
 * stringified JSON.
 *
 * @param - none
 * @returns (String) - The Stringified JSON of the keys from the valid list of
 *                     keys from the extractor function.
 */
function filterKeys() {
    var keys = { } ;

    keys.key1 = "pnr";
    keys.key2 = "train";
    keys.key3 = "status";

    return JSON.stringify(keys);
}

/**
 * Settings allows the user to specify the "friendly" names for the various keys
 * that are extracted via the extractor() method. These friendly names will
 * appear in the display when the application runs. In addition to this it is
 * also used to set if an extracted key is "displayed" or not in the front-end.
 *
 * @param - none
 * @retuens {String} = The Stringified JSON of the settings options set by the
 *                     user.
 */
function settings() {
    var settings = { } ;

    settings.display_name = { };
    settings.display_name.pnr = "PNR";
    settings.display_name.train = "Train";
    settings.display_name.name = "Name";
    settings.display_name.status = "Status";

    settings.is_displayed = { };
    settings.is_displayed.pnr = true;
    settings.is_displayed.train = true;
    settings.is_displayed.name = false;
    settings.is_displayed.status = true;

    return JSON.stringify(settings);
}
