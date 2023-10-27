package racingcar.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import racingcar.exception.position.NotExistPositionException;
import racingcar.util.NumberGenerator;

public class CarRacingGame implements RacingGame {

    private final CarRacingManager carRacingManager;

    private CarRacingGame(final CarRacingManager carRacingManager) {
        this.carRacingManager = carRacingManager;
    }

    public static CarRacingGame createWith(final CarRacingManager carRacingManager) {
        return new CarRacingGame(carRacingManager);
    }

    @Override
    public void move(final NumberGenerator numberGenerator) {
        carRacingManager.getCars().stream()
                .filter(car -> car.canMove(numberGenerator))
                .forEach(this::moveNextPosition);
    }

    private void moveNextPosition(final Car car) {
        Position position = carRacingManager.getPosition(car);
        Position nextPosition = position.getNextPosition();
        carRacingManager.changePosition(car, nextPosition);
    }

    @Override
    public Map<String, Integer> getResult() {
        Map<String, Integer> vehiclePositionMap = new LinkedHashMap<>();
        for (Map.Entry<Car, Position> entry : carRacingManager.getCarPositionEntries()) {
            vehiclePositionMap.put(entry.getKey().getName(), entry.getValue().getPositionIndex());
        }
        return vehiclePositionMap;
    }

    @Override
    public List<String> getWinner() {
        int maxPosition = getMaxPosition();
        return calculateWinners(maxPosition);
    }

    private int getMaxPosition() {
        return carRacingManager.getPositions().stream()
                .mapToInt(Position::getPositionIndex)
                .max()
                .orElseThrow(NotExistPositionException::new);
    }

    private List<String> calculateWinners(final int maxPosition) {
        return carRacingManager.getCarPositionEntries().stream()
                .filter(entry -> entry.getValue().getPositionIndex() == maxPosition)
                .map(entry -> entry.getKey().getName())
                .toList();
    }
}
