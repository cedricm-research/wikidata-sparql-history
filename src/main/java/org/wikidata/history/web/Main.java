/*
 * Copyright (C) 2017 Simple WD Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wikidata.history.web;

import io.javalin.Javalin;
import org.apache.commons.cli.*;
import org.wikidata.history.sparql.MapDBTripleSource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) throws ParseException {
    Options options = new Options();
    options.addOption("i", "index", true, "Directory where the index data are.");
    options.addOption("h", "host", true, "Host name");
    options.addOption("p", "port", true, "Name of the port to listen from");

    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(options, args);
    Path indexPath = Paths.get(line.getOptionValue("index", "wd-history-index"));

    String portString = line.getOptionValue("port", System.getenv("PORT"));
    int port = (portString != null) ? Integer.valueOf(portString) : 7000;

    SparqlEndpoint sparqlEndpoint = new SparqlEndpoint(new MapDBTripleSource(indexPath));
    Javalin.create()
            .get("", ctx -> ctx.contentType("text/html").result(Main.class.getResourceAsStream("/index.html")))
            .get("/sparql", sparqlEndpoint::get)
            .post("/sparql", sparqlEndpoint::post)
            .start(port);
  }
}
