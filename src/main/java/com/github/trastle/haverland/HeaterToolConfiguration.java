package com.github.trastle.haverland;

import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class HeaterToolConfiguration extends Configuration {

    @NotNull
    private Haverland haverland;

    public Haverland getHaverland() {
        return haverland;
    }

    public class Haverland {

        @NotNull
        @NotEmpty
        private String username;

        @NotNull
        @NotEmpty
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}




