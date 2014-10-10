/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var dhis2 = dhis2 || {};
dhis2['util'] = dhis2['util'] || {};

/**
 * Creates namespace object based on path
 *
 * @param path {String} The path of the namespace, i.e. 'a.b.c'
 *
 * @returns {object} Namespace object
 */
dhis2.util.namespace = function( path ) {
    var parts = path.split('.');
    var parent = window;
    var currentPart = '';

    for( var i = 0, length = parts.length; i < length; i++ ) {
        currentPart = parts[i];
        parent[currentPart] = parent[currentPart] || {};
        parent = parent[currentPart];
    }

    return parent;
};

/**
 * Escape function for regular expressions.
 */
dhis2.util.escape = function( text ) {
    return text.replace(/[-[\]{}()*+?.,\/\\^$|#\s]/g, "\\$&");
};

/**
 * jQuery cannot correctly filter strings with () in them, so here is a fix
 * until jQuery gets updated.
 */
dhis2.util.jqTextFilterCaseSensitive = function( key, not ) {
    key = dhis2.util.escape(key);
    not = not || false;

    if( not ) {
        return function( i, el ) {
            return !!!$(el).text().match("" + key);
        };
    }
    else {
        return function( i, el ) {
            return !!$(el).text().match("" + key);
        };
    }
};

dhis2.util.jqTextFilter = function( key, not ) {
    key = dhis2.util.escape(key).toLowerCase();
    not = not || false;

    if( not ) {
        return function( i, el ) {
            return !!!$(el).text().toLowerCase().match("" + key);
        };
    }
    else {
        return function( i, el ) {
            return !!$(el).text().toLowerCase().match("" + key);
        };
    }
};

dhis2.util.uuid = function() {
    var S4 = function() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };

    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

/**
 * Normalizes an argument object returned from a jQuery promise. If the argument
 * is undefined, not an array or an empty array, undefined is returned. If the 
 * argument is a single promise object, the object is wrapped in an array. If the
 * argument is an array of promise objects, the array is returned unmodified.
 */
dhis2.util.normalizeArguments = function( args ) {
	if ( !args || !args.length || !args[0] ) {
		return undefined;
	}
	
	if ( $.isArray( args[0] ) ) {
		return args;
	}
	else {
		var arr = [];
		arr[0] = args;
		return arr;
	}
}

/**
 * adds ':containsNC' to filtering.
 * $(sel).find(':containsNC(key)').doSomething();
 */
$.expr[":"].containsNC = function( a, i, m, r ) {
    var search = dhis2.util.escape(m[3]);
    return jQuery(a).text().toUpperCase().indexOf(m[search].toUpperCase()) >= 0;
};

/**
 * adds ':regex' to filtering, use to filter by regular expression
 */
$.expr[":"].regex = function( a, i, m, r ) {
    var re = new RegExp(m[3], 'i');
    return re.test(jQuery(a).text());
};

/**
 * adds ':regex' to filtering, use to filter by regular expression
 *
 * (this is the case sensitive version)
 */
$.expr[":"].regexCS = function( a, i, m, r ) {
    var re = new RegExp(m[3]);
    return re.test(jQuery(a).text());
};

/**
 * Returns an array of the keys in a given object. Will use ES5 Object.keys() if
 * available, if not it will provide a pure javascript implementation.
 *
 * @returns array of keys
 */
if( !Object.keys ) {
    Object.keys = function( obj ) {
        var keys = new Array();
        for( k in obj )
            if( obj.hasOwnProperty(k) )
                keys.push(k);
        return keys;
    };
}

/**
 * Define a window.log object, and output to console.log if it exists. (this is
 * a fix for IE8 and FF 3.6).
 */
window.log = function( str ) {
    if( this.console ) {
        console.log(str);
    }
};
