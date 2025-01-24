package CognitiveModel.ModelFiles.XPCdependencies;

import CognitiveModel.ModelFiles.*;

public interface DiscoveryConnectionCallback {

    /**
     * Helper callback called when a 1st XPlanePlugin is discovered. When the 1st packet is received, an XPlaneConnect
     * instance is created and the discovery is stopped.
     *
     * @param xpc The XPlaneConnect instance configured with the discovered
     */
    void onConnectionEstablished(XPlaneConnect xpc);
}
