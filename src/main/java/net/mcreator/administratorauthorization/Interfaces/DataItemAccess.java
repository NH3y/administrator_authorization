package net.mcreator.administratorauthorization.Interfaces;

public interface DataItemAccess<T> {
    void administrator_authorization$indexingItem();

    void administrator_authorization$dirty();

    T administrator_authorization$directlyInteract(boolean toWrite, T value);

    void administrator_authorization$toLock(int level);

    int administrator_authorization$getLock();

    T administrator_authorization$getLockValue();

    void administrator_authorization$setLockValue(T value);
}
