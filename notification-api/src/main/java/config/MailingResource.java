package config;

import dto.OrderNotificationDTO;
import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;

public class MailingResource {

  @CheckedTemplate
  public static class Templates {

    public static native MailTemplateInstance orderConfirmation(OrderNotificationDTO order);
  }
}
