# vim: syntax=ruby ts=2 sw=2 noexpandtab
repositories.remote << 'file:///home/pbourke/.m2/repository'

define 'form-server' do
	project.version = '0.0.1'

	define 'impl' do
		package :jar
	end

	define 'fonts' do
		package :jar
	end

	define 'web' do
		package :war
	end
end
