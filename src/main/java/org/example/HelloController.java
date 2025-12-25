package org.example;

import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Spring Boot + Vendor JARs (Port 33333) ✅";
    }

    @PostMapping("/write")
    public String writeFile(
            @RequestParam(defaultValue = "test.txt") String name,
            @RequestParam(defaultValue = "from springboot repo") String content
    ) throws IOException {
        Path target = Paths.get("/tmp", name);
        Files.writeString(target, content + "\n",
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        return "✅ Written to " + target;
    }

    @GetMapping("/read/{name}")
    public String readFile(@PathVariable String name) throws IOException {
        Path target = Paths.get("/tmp", name);
        if (!Files.exists(target)) {
            return "❌ File not found: " + target;
        }
        return Files.readString(target);
    }
}
