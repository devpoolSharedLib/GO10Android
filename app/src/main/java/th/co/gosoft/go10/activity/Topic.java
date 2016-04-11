package th.co.gosoft.go10.activity;

/**
 * Created by jintanasog on 3/11/2016.
 */
public class Topic {

    private String topic;
    private String description;

    public Topic(String topic, String description) {
        super();
        this.topic = topic;
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}