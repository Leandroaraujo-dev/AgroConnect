package com.example.agroconnect.controller;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Colheita")

public class ColheitaController{
    @GetMapping
public String ColheitaController(@PathVariable long id) {
        return "colheita" + id;
    }
}



