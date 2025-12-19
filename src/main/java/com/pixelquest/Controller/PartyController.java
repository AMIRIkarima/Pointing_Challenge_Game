package com.pixelquest.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.pixelquest.Entity.Party;
import com.pixelquest.Entity.Difficulty;


import com.pixelquest.Service.PartyService;

import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/parties")
@CrossOrigin(origins = "*")
public class PartyController {

    @Autowired
    private PartyService partyService;

    // STEP 1: Mobile App calls this to start a game with a specific difficulty
    @PostMapping("/start")
    public Party startParty(@RequestParam Long playerId, @RequestParam Difficulty difficulty) {
        return partyService.startParty(playerId, difficulty);
    }

    // STEP 2: ESP32 calls this to fetch the mission coordinates (Target X, Y)
    @GetMapping("/active/{playerId}")
    public Party getActiveParty(@PathVariable Long playerId) {
        return partyService.getActivePartyForPlayer(playerId);
    }

    // STEP 3: ESP32 calls this once to send final Time and Distance
    @PostMapping("/{partyId}/direct-finish")
    public Party finishDirect(@PathVariable Long partyId, @RequestBody Map<String, Double> data) {
        return partyService.finishPartyWithDirectData(
                partyId,
                data.get("time_sec"),
                data.get("distance")
        );
    }
}