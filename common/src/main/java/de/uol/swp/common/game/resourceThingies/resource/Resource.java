package de.uol.swp.common.game.resourceThingies.resource;

/**
 * The type Mutable resource.
 *
 * @author Temmo Junkhoff
 * @since 2021-04-23
 */
public class Resource implements IResource {

    private final ResourceType type;
    private int amount;

    /**
     * Instantiates a new Mutable resource.
     *
     * @param type   the type
     * @param amount the amount
     *
     * @author Temmo Junkhoff
     * @since 2021-04-23
     */
    public Resource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public Resource create() {
        return new Resource(getType(), getAmount());
    }

    @Override
    public void decrease(int amount) {
        increase(-amount);
    }

    @Override
    public void decrease() {
        decrease(-1);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    @Override
    public void increase(int amount) {
        setAmount(getAmount() + amount);
    }

    @Override
    public void increase() {
        increase(1);
    }
}
