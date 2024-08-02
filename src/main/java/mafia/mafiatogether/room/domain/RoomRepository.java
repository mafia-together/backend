package mafia.mafiatogether.room.domain;

public interface RoomRepository {

    String create(final RoomInfo roomInfo);
    Room findByCode(final String code);
    boolean validateCode(final String code);
    Integer getTotalRoomCount();

    Room save(Room room);

    void deleteById(String code);
}
