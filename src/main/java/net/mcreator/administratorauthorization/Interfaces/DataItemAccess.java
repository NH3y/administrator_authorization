package net.mcreator.administratorauthorization.Interfaces;

public interface DataItemAccess<T> {
    void administrator_authorization$setProtected(boolean value);

    void administrator_authorization$indexingItem();

    void administrator_authorization$dirty();

    T administrator_authorization$directlyInteract(boolean toWrite, T value);

    void administrator_authorization$toLock(int level);
}
