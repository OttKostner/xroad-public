# -*- mode: ruby -*-
# vi: set ft=ruby :

# Minimal build environment for X-Road security server
# Tested with Virtualbox 5 provider on Ubuntu host
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.box = "ubuntu/trusty64"

    config.vm.provider :virtualbox do |vb|
        vb.name="xroad-build-host"
        vb.customize ["modifyvm", :id, "--memory", "2048"]
    end

    $script = <<SCRIPT
cd /vagrant/src/
echo "Installing prerequisites..."
bash prepare_buildhost.sh &>/dev/null
echo "Updating ruby dependencies..."
bash update_ruby_dependencies.sh &>/dev/null
SCRIPT

    config.vm.provision :shell, :privileged => false, :inline => $script

end

