package org.example;

import org.apache.commons.io.FileUtils;  // From system scope lib/
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actimize")
public class VendorController {

    @GetMapping("/test")
    public String testActimizeJAR() throws Exception {
        // Simulate Actimize usage
        FileUtils.writeStringToFile(
            new java.io.File("/tmp/actimize-test.txt"),
            "Actimize JAR (system scope) WORKS! ✅",
            "UTF-8", true
        );
        return "✅ Actimize JAR loaded via system scope + Docker lib/";
    }
}
