package resourceServer;

import resources.Resource;

public class ResourceServer implements ResourceServerMBean {
    private Resource resource;

    public ResourceServer(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getName() {
        return resource.getName();
    }

    @Override
    public int getAge() {
        return resource.getAge();
    }

}
