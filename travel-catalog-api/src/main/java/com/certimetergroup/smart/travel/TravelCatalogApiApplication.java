package com.certimetergroup.smart.travel;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class TravelCatalogApiApplication {

    public static void main(String ... args) {
        System.out.println("Running Travel Catalog API");
        Quarkus.run(args);
    }
}