package features.filters.experimental.servletContract

import framework.ReposeValveTest
import org.rackspace.deproxy.Deproxy
import org.rackspace.deproxy.MessageChain

class ServletContractFilterTest extends ReposeValveTest {

    def setupSpec() {
        def params = properties.defaultTemplateParams
        repose.configurationProvider.applyConfigs("common", params)
        repose.configurationProvider.applyConfigs("features/filters/experimental/servletfilter", params)

        deproxy = new Deproxy()
        deproxy.addEndpoint(properties.targetPort)

        repose.start([waitOnJmxAfterStarting: false])
        waitUntilReadyToServiceRequests("200")
    }

    def cleanupSpec() {
        repose.stop()
        deproxy.shutdown()
    }

    /**
     * This test fails because repose does not properly support the servlet filter contract
     * @return
     */
    def "Proving that a custom filter does in fact work" () {
        when:
        MessageChain mc = null
        mc = deproxy.makeRequest(
                [
                        method: 'GET',
                        url:reposeEndpoint + "/get",
                ])


        then:
        mc.receivedResponse.code == '200'
        mc.receivedResponse.body.contains("<extra> Added by TestFilter, should also see the rest of the content </extra>")
    }
}
