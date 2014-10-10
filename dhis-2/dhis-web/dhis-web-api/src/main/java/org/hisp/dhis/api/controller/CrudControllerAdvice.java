package org.hisp.dhis.api.controller;

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

import org.hisp.dhis.api.controller.exception.NotAuthenticatedException;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.controller.exception.NotFoundForQueryException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@ControllerAdvice
public class CrudControllerAdvice
{
    @ExceptionHandler
    public ResponseEntity<String> notAuthenticatedExceptionHandler( NotAuthenticatedException ex )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.TEXT_PLAIN_VALUE );

        return new ResponseEntity<String>( ex.getMessage(), headers, HttpStatus.UNAUTHORIZED );
    }

    @ExceptionHandler({ NotFoundException.class, NotFoundForQueryException.class })
    public ResponseEntity<String> notFoundExceptionHandler( Exception ex )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.TEXT_PLAIN_VALUE );

        return new ResponseEntity<String>( ex.getMessage(), headers, HttpStatus.NOT_FOUND );
    }

    @ExceptionHandler( { HttpClientErrorException.class, HttpServerErrorException.class } )
    public ResponseEntity<String> httpClient( HttpStatusCodeException ex )
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type", MediaType.TEXT_PLAIN_VALUE );

        return new ResponseEntity<String>( ex.getStatusText(), headers, ex.getStatusCode() );
    }
}
