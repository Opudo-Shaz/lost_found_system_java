package com.example.lostandfound.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController

public class LoggingController {


    private static final Logger logger = LogManager.getLogger(LoggingController.class);

        /**
         * Demonstrates logging at various levels using Log4j2.
         *
         * @return A message indicating the demonstration is complete.
         */
        @GetMapping("/log")
        public String logger() {
            logger.trace("This is a TRACE level log - typically used for detailed debugging information.");
            logger.debug("This is a DEBUG level log - useful for debugging application flow.");
            logger.info("This is an INFO level log - general information about application operations.");
            logger.warn("This is a WARN level log - indicates a potential issue or important warning.");
            logger.error("This is an ERROR level log - indicates a failure or significant problem.", new Exception("Example exception"));
            logger.fatal("This is a FATAL level log - indicates a critical error or system crash.");

            return "Logging demonstration completed! Check the console and log file for output.";
        }
    }


