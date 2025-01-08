package pl.arkadiusz.urbanski.ideas.handlers;

abstract class BaseCommandHandler implements CommandHandler {


  @Override
  public boolean supports(String name) {
    return getCommandName().equals(name);
  }

  protected abstract String getCommandName();
}
