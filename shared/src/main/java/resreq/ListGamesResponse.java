package resreq;

import model.GameData;

import java.util.Vector;

public record ListGamesResponse(Vector<GameData> games) {
}
