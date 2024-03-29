/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package javaguide.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import play.i18n.Lang;
import play.i18n.Langs;
import play.i18n.Messages;
import play.i18n.MessagesApi;

public class MessagesTest {

  // #test-messages
  @Test
  public void renderMessages() {
    Langs langs = new Langs(new play.api.i18n.DefaultLangs());

    Map<String, String> messagesMap = Collections.singletonMap("foo", "bar");
    Map<String, Map<String, String>> langMap =
        Collections.singletonMap(Lang.defaultLang().code(), messagesMap);
    MessagesApi messagesApi = play.test.Helpers.stubMessagesApi(langMap, langs);

    Messages messages = messagesApi.preferred(langs.availables());
    assertEquals(messages.at("foo"), "bar");
  }
  // #test-messages

}
